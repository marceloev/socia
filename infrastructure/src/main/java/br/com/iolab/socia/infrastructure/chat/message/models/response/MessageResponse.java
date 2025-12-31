package br.com.iolab.socia.infrastructure.chat.message.models.response;

import br.com.iolab.socia.domain.chat.ChatID;
import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.MessageID;
import br.com.iolab.socia.domain.chat.message.types.MessageContent;
import br.com.iolab.socia.domain.chat.message.types.MessageRoleType;
import br.com.iolab.socia.domain.chat.message.types.MessageStatusType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

import java.time.Instant;

public record MessageResponse(
        @JsonProperty(value = "id") MessageID id,
        @JsonProperty(value = "chat_id") ChatID chatID,
        @JsonProperty(value = "status") MessageStatusType status,
        @JsonProperty(value = "role") MessageRoleType role,
        @JsonProperty(value = "content") MessageContent content,
        @JsonProperty(value = "created_at") Instant createdAt
) {
    public static MessageResponse from(@NonNull final Message message) {
        return new MessageResponse(
                message.getId(),
                message.getChatID(),
                message.getStatus(),
                message.getRole(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}
