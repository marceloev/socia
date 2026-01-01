package br.com.iolab.socia.infrastructure.assistant.instance.strategy.whatsapp;

import br.com.iolab.socia.domain.assistant.instance.Instance;
import br.com.iolab.socia.domain.assistant.instance.InstanceStrategy;
import br.com.iolab.socia.domain.chat.Chat;
import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.resource.MessageResource;
import br.com.iolab.socia.infrastructure.assistant.instance.strategy.whatsapp.client.WhatsappClient;
import br.com.iolab.socia.infrastructure.assistant.instance.strategy.whatsapp.request.SendWhatsappMessageRequest;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class WhatsappInstanceStrategy implements InstanceStrategy {
    private final WhatsappClient whatsappClient;

    @Override
    public void send (
            @NonNull final Instance instance,
            @NonNull final Chat chat,
            @NonNull final Collection<Message> messages
    ) {
        if (messages.isEmpty()) {
            return;
        }

        messages.forEach(message -> this.whatsappClient.send(
                instance.getId().toString(),
                SendWhatsappMessageRequest.with(
                        chat.getFrom().value(),
                        message.getContent().value()
                )
        ));
    }

    @Override
    public MessageResource retrieveFile (@NonNull final String fileId) {
        return this.whatsappClient.retrieveFile(fileId);
    }
}
