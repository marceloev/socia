package br.com.iolab.socia.infrastructure.chat.message.strategy;

import br.com.iolab.socia.domain.assistant.Assistant;
import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.MessageStrategy;
import br.com.iolab.socia.domain.chat.message.resource.MessageResource;
import br.com.iolab.socia.infrastructure.chat.message.strategy.gemini.GeminiMessageStrategy;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;

@Primary
@RequiredArgsConstructor
public class DefaultMessageStrategy implements MessageStrategy {
    private final GeminiMessageStrategy geminiMessageStrategy;

    @Override
    public @NonNull Message perform (
            @NonNull final Assistant assistant,
            @NonNull final Message message,
            @NonNull final Iterable<MessageResource> resources
    ) {
        return switch (assistant.getProvider()) {
            case GEMINI -> this.geminiMessageStrategy.perform(assistant, message, resources);
        };
    }
}
