package br.com.iolab.socia.infrastructure.chat.message.strategy;

import br.com.iolab.socia.domain.assistant.Assistant;
import br.com.iolab.socia.domain.chat.Chat;
import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.strategy.MessageStrategy;
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
    public @NonNull Message perform (
            @NonNull final Assistant assistant,
            @NonNull final Chat chat
    ) {
        log.debug("Performing message strategy...");
        return switch (assistant.getProvider()) {
            case GEMINI -> this.geminiMessageStrategy.perform(assistant, chat);
        };
    }
}
