package br.com.iolab.socia.infrastructure.config;

import br.com.iolab.commons.application.registry.UseCaseRegistry;
import br.com.iolab.commons.application.transactional.TransactionalExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    protected UseCaseRegistry useCaseRegistry (
            final TransactionalExecutor transactionalExecutor
    ) {
        return UseCaseRegistry.builder()
                .transactionalExecutor(transactionalExecutor)
                .build();
    }
}

