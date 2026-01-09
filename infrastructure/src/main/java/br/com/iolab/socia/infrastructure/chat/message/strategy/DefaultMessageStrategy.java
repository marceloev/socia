package br.com.iolab.socia.infrastructure.chat.message.strategy;

import br.com.iolab.socia.domain.chat.message.strategy.MessageStrategy;
import br.com.iolab.socia.domain.chat.message.strategy.perform.PerformMessageStrategyInput;
import br.com.iolab.socia.domain.chat.message.strategy.perform.PerformMessageStrategyOutput;
import br.com.iolab.socia.infrastructure.chat.message.strategy.gemini.GeminiMessageStrategy;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class DefaultMessageStrategy implements MessageStrategy {
    private final GeminiMessageStrategy geminiMessageStrategy;

    @Override
    public @NonNull PerformMessageStrategyOutput perform (@NonNull final PerformMessageStrategyInput input) {
        log.debug("Performing message strategy...");
        return switch (input.getAssistant().getProvider()) {
            case GEMINI -> this.geminiMessageStrategy.perform(input);
        };
    }
}
