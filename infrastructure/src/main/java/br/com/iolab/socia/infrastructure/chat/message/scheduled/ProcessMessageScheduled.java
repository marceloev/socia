package br.com.iolab.socia.infrastructure.chat.message.scheduled;

import br.com.iolab.socia.application.chat.message.process.ProcessMessageUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessMessageScheduled {
    private final ProcessMessageUseCase processMessageUseCase;

    @Scheduled(fixedDelay = 3, timeUnit = TimeUnit.SECONDS)
    protected void process () {
        this.processMessageUseCase.execute();
    }
}
