package br.com.iolab.socia.application.chat.message.process;

import br.com.iolab.commons.domain.utils.ExceptionUtils;
import br.com.iolab.commons.domain.validation.Result;
import br.com.iolab.commons.types.Streams;
import br.com.iolab.socia.application.chat.message.perform.PerformMessageUseCase;
import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.MessageGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static br.com.iolab.commons.domain.utils.InstantUtils.now;
import static br.com.iolab.commons.domain.utils.InstantUtils.plusMinutes;

@Slf4j
@RequiredArgsConstructor
public class ProcessMessageUseCaseImpl extends ProcessMessageUseCase {
    private final MessageGateway messageGateway;
    private final PerformMessageUseCase performMessageUseCase;

    @Override
    protected void perform () {
        var messagesByChat = this.transactional().getTransactionalExecutor().execute(() ->
                this.messageGateway.reserve(10, now(), plusMinutes(10))
        ).stream().collect(Collectors.groupingBy(Message::getChatID));

        if (messagesByChat.isEmpty()) {
            log.debug("No messages found...");
            return;
        }

        messagesByChat.forEach((chatID, messages) -> {
            var lastMessage = messages.stream()
                    .max(Comparator.comparing(Message::getCreatedAt))
                    .orElseThrow(ExceptionUtils.badRequest("Não foi possível determinar a última mensagem do chat: " + chatID.value()));

            CompletableFuture.runAsync(() -> this.performMessageUseCase.execute(new PerformMessageUseCase.Input(lastMessage)))
                    .thenAccept(_ -> {
                        var processedMessages = Streams.streamOf(messages)
                                .map(Message::markAsProcessed)
                                .map(Result::successOrThrow)
                                .toList();

                        this.update(this.messageGateway, processedMessages);
                    })
                    .orTimeout(2, TimeUnit.MINUTES)
                    .exceptionally(throwable -> {
                        log.error("Error while trying to process messages from chat: {}", chatID, throwable);

                        var failedMessages = Streams.streamOf(messages)
                                .map(Message::markAsFailed)
                                .map(Result::successOrThrow)
                                .toList();

                        this.update(this.messageGateway, failedMessages);
                        return null;
                    });
        });
    }
}
