package br.com.iolab.socia.infrastructure.chat.message.scheduled;

import br.com.iolab.socia.application.chat.message.process.ProcessMessageUseCase;
import br.com.iolab.socia.application.chat.message.send.SendMessageUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageScheduled {
    private final ProcessMessageUseCase processMessageUseCase;
    private final SendMessageUseCase sendMessageUseCase;

    @Scheduled(fixedDelay = 3, timeUnit = TimeUnit.SECONDS)
    protected void process () {
        this.processMessageUseCase.execute();
    }

    @Scheduled(fixedDelay = 3, timeUnit = TimeUnit.SECONDS)
    protected void send () {
        this.sendMessageUseCase.execute();
    }
}
