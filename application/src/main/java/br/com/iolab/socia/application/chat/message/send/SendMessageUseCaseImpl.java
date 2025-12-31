package br.com.iolab.socia.application.chat.message.send;

import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.MessageGateway;
import br.com.iolab.socia.domain.chat.message.valueobject.ReservationPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.stream.Collectors;

import static br.com.iolab.socia.domain.chat.message.types.MessageStatusType.COMPLETED;

@Slf4j
@RequiredArgsConstructor
public class SendMessageUseCaseImpl extends SendMessageUseCase {
    private final MessageGateway messageGateway;

    @Override
    protected void perform () {
        var messagesByChat = this.transactional().getTransactionalExecutor().execute(() ->
                this.messageGateway.reserve(ReservationPolicy.with(COMPLETED, 10, Duration.ofMinutes(5)))
        ).stream().collect(Collectors.groupingBy(Message::getChatID));

        if (messagesByChat.isEmpty()) {
            log.debug("No messages found...");
        }

        //Send

    }
}
