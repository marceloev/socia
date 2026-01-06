package br.com.iolab.socia.infrastructure.assistant.knowledge.usecase;

import br.com.iolab.socia.application.assistant.knowledge.delete.DeleteExpiredKnowledgeUseCase;
import br.com.iolab.socia.application.assistant.knowledge.delete.DeleteExpiredKnowledgeUseCaseImpl;
import br.com.iolab.socia.domain.assistant.knowledge.KnowledgeGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KnowledgeUseCaseConfig {
    private final KnowledgeGateway knowledgeGateway;

    @Bean
    protected DeleteExpiredKnowledgeUseCase deleteExpiredKnowledgeUseCase() {
        return new DeleteExpiredKnowledgeUseCaseImpl(knowledgeGateway);
    }
}
