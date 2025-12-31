package br.com.iolab.socia.application.chat.message.process;

import br.com.iolab.commons.domain.validation.Result;
import br.com.iolab.commons.types.Streams;
import br.com.iolab.socia.application.chat.message.perform.PerformMessageUseCase;
import br.com.iolab.socia.domain.assistant.AssistantGateway;
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

import static br.com.iolab.socia.domain.chat.message.types.MessageStatusType.RECEIVED;

@Slf4j
@RequiredArgsConstructor
public class ProcessMessageUseCaseImpl extends ProcessMessageUseCase {
    private final MessageGateway messageGateway;
    private final AssistantGateway assistantGateway;
    private final PerformMessageUseCase performMessageUseCase;

    @Override
    protected void perform () {
        var messagesByChat = this.transactional().getTransactionalExecutor().execute(() ->
                this.messageGateway.reserve(ReservationPolicy.with(RECEIVED, 10, Duration.ofMinutes(5)))
        ).stream().collect(Collectors.groupingBy(Message::getChatID));

        if (messagesByChat.isEmpty()) {
            log.debug("No messages found...");
            return;
        }

        var changeBus = bus();

        var completable = new ArrayList<CompletableFuture<?>>();
        messagesByChat.forEach((chatID, messages) -> {
            completable.add(CompletableFuture.runAsync(() -> this.performMessageUseCase.execute(new PerformMessageUseCase.Input(chatID)))
                    .thenAccept(_ -> Streams.streamOf(messages)
                            .map(Message::markAsProcessed)
                            .map(Result::successOrThrow)
                            .forEach(message -> changeBus.add(this.messageGateway::update, message))
                    )
                    .orTimeout(2, TimeUnit.MINUTES)
                    .exceptionally(throwable -> {
                        log.error("Error while trying to process messages from chat: {}", chatID, throwable);

                        Streams.streamOf(messages)
                                .map(Message::markAsFailed)
                                .map(Result::successOrThrow)
                                .forEach(message -> changeBus.add(this.messageGateway::update, message));

                        return null;
                    }));
        });

        CompletableFuture.allOf(completable.toArray(new CompletableFuture[0])).join();
    }
}
