package br.com.iolab.socia.infrastructure.chat.message.models.request;

import br.com.iolab.socia.application.chat.message.receive.ReceiveMessageWhatsAppUseCase;
import br.com.iolab.socia.domain.assistant.instance.InstanceID;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateMessageWhatsAppRequest(
        @JsonProperty(value = "instance", required = true) InstanceID id,
        @JsonProperty(value = "event", required = true) String event,
        @JsonProperty(value = "chat", required = true) String chat,
        @JsonProperty(value = "text") String text,
        @JsonProperty(value = "fileId") String fileId
) {
    public ReceiveMessageWhatsAppUseCase.Input toInput () {
        return new ReceiveMessageWhatsAppUseCase.Input(id(), event(), chat(), text(), fileId());
    }
}
