package br.com.iolab.socia.infrastructure.assistant.instance.strategy.whatsapp;

import br.com.iolab.commons.domain.exceptions.BadRequestException;
import br.com.iolab.socia.domain.assistant.instance.Instance;
import br.com.iolab.socia.domain.assistant.instance.InstanceStrategy;
import br.com.iolab.socia.domain.chat.Chat;
import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.infrastructure.assistant.instance.strategy.whatsapp.client.WhatsappClient;
import br.com.iolab.socia.infrastructure.assistant.instance.strategy.whatsapp.request.SendWhatsappMessageRequest;
import br.com.iolab.socia.infrastructure.assistant.instance.strategy.whatsapp.response.SendWhatsappMessageResponse;
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

        for (var message : messages) {
            var request = SendWhatsappMessageRequest.with(
                    chat.getFrom().value(),
                    message.getContent().value()
            );

            var response = this.whatsappClient.newRequest()
                    .pathParam("sessionId", instance.getId().toString())
                    .post(request)
                    .execute(SendWhatsappMessageResponse.class)
                    .orElseThrow();

            if (!response.isSuccessful() || !response.body().success()) {
                throw BadRequestException.with("Ocorreu um erro inesperado ao tentar enviar a mensagem!");
            }
        }
    }
}
