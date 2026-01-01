package br.com.iolab.socia.application.chat.message.send;

import br.com.iolab.commons.domain.utils.ExceptionUtils;
import br.com.iolab.commons.domain.validation.Result;
import br.com.iolab.commons.types.Streams;
import br.com.iolab.socia.domain.assistant.instance.Instance;
import br.com.iolab.socia.domain.assistant.instance.InstanceGateway;
import br.com.iolab.socia.domain.assistant.instance.InstanceStrategy;
import br.com.iolab.socia.domain.chat.Chat;
import br.com.iolab.socia.domain.chat.ChatGateway;
import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.MessageGateway;
import br.com.iolab.socia.domain.chat.message.valueobject.ReservationPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static br.com.iolab.socia.domain.chat.message.types.MessageStatusType.COMPLETED;

@Slf4j
@RequiredArgsConstructor
public class SendMessageUseCaseImpl extends SendMessageUseCase {
    private final ChatGateway chatGateway;
    private final MessageGateway messageGateway;
    private final InstanceGateway instanceGateway;
    private final InstanceStrategy instanceStrategy;

    @Override
    protected void perform () {
        var messagesByChat = this.transactional().getTransactionalExecutor().execute(() ->
                this.messageGateway.reserve(ReservationPolicy.with(COMPLETED, 10, Duration.ofMinutes(5)))
        ).stream().collect(Collectors.groupingBy(Message::getChatID));

        if (messagesByChat.isEmpty()) {
            return;
        }

        var changeBus = bus();

        var completable = new ArrayList<CompletableFuture<?>>();
        messagesByChat.forEach((chatID, messages) -> completable.add(
                CompletableFuture.runAsync(() -> {
                            var chat = this.chatGateway.findById(chatID)
                                    .orElseThrow(ExceptionUtils.notFound(chatID, Chat.class));

                            var instance = this.instanceGateway.findById(chat.getInstanceID())
                                    .orElseThrow(ExceptionUtils.notFound(chat.getInstanceID(), Instance.class));

                            this.instanceStrategy.send(instance, chat, messages);
                        })
                        .thenAccept(_ -> Streams.streamOf(messages)
                                .map(Message::markAsFailed)
                                .map(Result::successOrThrow)
                                .forEach(message -> changeBus.add(this.messageGateway::update, message))
                        )
                        .orTimeout(2, TimeUnit.MINUTES)
                        .exceptionally(throwable -> {
                            log.error("Error while trying to process messages from chat: {}", chatID, throwable);

                            Streams.streamOf(messages)
                                    .map(Message::markAsSent)
                                    .map(Result::successOrThrow)
                                    .forEach(message -> changeBus.add(this.messageGateway::update, message));

                            return null;
                        }))
        );

        CompletableFuture.allOf(completable.toArray(new CompletableFuture[0])).join();
    }
}
