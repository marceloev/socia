package br.com.iolab.socia.infrastructure.chat.message.models.request;

import br.com.iolab.commons.types.fields.Phone;
import br.com.iolab.socia.application.chat.message.create.CreateMessageUseCase;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

public record CreateMessageRequest(
        @NotNull @JsonProperty(value = "to") Phone to,
        @NotNull @JsonProperty(value = "from") Phone from,
        @NotBlank @JsonProperty(value = "content") String content
) {
    public @NonNull CreateMessageUseCase.Input toInput () {
        return new CreateMessageUseCase.Input(to(), from(), content());
    }
}
