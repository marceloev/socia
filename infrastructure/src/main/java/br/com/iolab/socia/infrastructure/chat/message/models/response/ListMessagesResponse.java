package br.com.iolab.socia.infrastructure.chat.message.models.response;

import br.com.iolab.socia.application.chat.message.list.ListMessagesByChatUseCase;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

import java.util.List;

public record ListMessagesResponse(
        @JsonProperty(value = "messages") List<MessageResponse> messages
) {
    public static ListMessagesResponse present(@NonNull final ListMessagesByChatUseCase.Output output) {
        return new ListMessagesResponse(
                output.messages().stream()
                        .map(MessageResponse::from)
                        .toList()
        );
    }
}
