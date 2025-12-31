package br.com.iolab.socia.infrastructure.chat.message.strategy.gemini;

import br.com.iolab.socia.domain.assistant.Assistant;
import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.MessageStrategy;
import br.com.iolab.socia.domain.chat.message.resource.MessageResource;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GeminiMessageStrategy implements MessageStrategy {
    @Override
    public @NonNull Message perform (
            @NonNull final Assistant assistant,
            @NonNull final Message message,
            @NonNull final Iterable<MessageResource> resources
    ) {
        return null;
    }
}
