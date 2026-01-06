package br.com.iolab.socia.application.assistant.knowledge.delete;

import br.com.iolab.socia.domain.assistant.knowledge.KnowledgeGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DeleteExpiredKnowledgeUseCaseImpl extends DeleteExpiredKnowledgeUseCase {
    private final KnowledgeGateway knowledgeGateway;

    @Override
    public void perform () {
        log.info("Starting deletion of expired knowledge");

        try {
            knowledgeGateway.deleteExpired();
            log.info("Successfully deleted expired knowledge");
        } catch (Exception e) {
            log.error("Error deleting expired knowledge", e);
            throw e;
        }
    }
}
