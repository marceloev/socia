package br.com.iolab.socia.infrastructure.chat.message.models.request;

import br.com.iolab.commons.types.fields.Phone;
import br.com.iolab.socia.application.chat.message.create.CreateMessageUseCase;
import br.com.iolab.socia.domain.chat.message.types.MessageContent;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

public record CreateMessageRequest(
        @JsonProperty(value = "to", required = true) Phone to,
        @JsonProperty(value = "from", required = true) Phone from,
        @JsonProperty(value = "content", required = true) MessageContent content
) {
    public @NonNull CreateMessageUseCase.Input toInput () {
        return new CreateMessageUseCase.Input(to(), from(), content());
    }
}
