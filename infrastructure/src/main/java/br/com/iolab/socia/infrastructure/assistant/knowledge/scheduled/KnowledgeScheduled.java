package br.com.iolab.socia.infrastructure.assistant.knowledge.scheduled;

import br.com.iolab.socia.application.assistant.knowledge.delete.DeleteExpiredKnowledgeUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KnowledgeScheduled {
    private final DeleteExpiredKnowledgeUseCase deleteExpiredKnowledgeUseCase;

    @Scheduled(cron = "0 0 3 * * *")
    protected void deleteExpired () {
        this.deleteExpiredKnowledgeUseCase.execute();
    }
}
