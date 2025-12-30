package br.com.iolab.socia.infrastructure.config;

import br.com.iolab.commons.application.transactional.TransactionalExecutor;
import br.com.iolab.commons.infrastructure.config.BasicTransactionalExecutorConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TXExecutorConfig {

    @Bean
    protected TransactionalExecutor transactionalExecutor (final PlatformTransactionManager platformTransactionManager) {
        return new BasicTransactionalExecutorConfig(platformTransactionManager);
    }
}
