package br.com.iolab.socia.infrastructure.chat.message.models.response;

import br.com.iolab.socia.application.chat.message.create.CreateMessageUseCase;
import br.com.iolab.socia.domain.chat.ChatID;
import br.com.iolab.socia.domain.chat.message.MessageID;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

public record CreateMessageResponse(
        @JsonProperty(value = "id") MessageID id,
        @JsonProperty(value = "chat_id") ChatID chatID
) {
    public static CreateMessageResponse present(@NonNull final CreateMessageUseCase.Output output) {
        return new CreateMessageResponse(
                output.messageID(),
                output.chatID()
        );
    }
}
