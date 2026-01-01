package br.com.iolab.socia.infrastructure.assistant.instance.strategy;

import br.com.iolab.socia.domain.assistant.instance.Instance;
import br.com.iolab.socia.domain.assistant.instance.InstanceStrategy;
import br.com.iolab.socia.domain.chat.Chat;
import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.resource.MessageResource;
import br.com.iolab.socia.infrastructure.assistant.instance.strategy.whatsapp.WhatsappInstanceStrategy;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Primary
@Component
@RequiredArgsConstructor
public class DefaultInstanceStrategy implements InstanceStrategy {
    private final WhatsappInstanceStrategy whatsappInstanceStrategy;

    @Override
    public void send (
            @NonNull final Instance instance,
            @NonNull final Chat chat,
            @NonNull final Collection<Message> message
    ) {
        switch (instance.getOrigin()) {
            case WHATSAPP -> this.whatsappInstanceStrategy.send(instance, chat, message);
        }
    }

    @Override
    public MessageResource retrieveFile (
            @NonNull final Instance instance,
            @NonNull final String fileId
    ) {
        return switch (instance.getOrigin()) {
            case WHATSAPP -> this.whatsappInstanceStrategy.retrieveFile(fileId);
        };
    }
}
