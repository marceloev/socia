package br.com.iolab.socia.infrastructure.chat.message.scheduled;

import br.com.iolab.socia.domain.chat.message.MessageGateway;
import br.com.iolab.socia.domain.chat.message.resource.MessageResource;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageScheduled {
    private final MessageGateway messageGateway;

}
