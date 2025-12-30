package br.com.iolab.socia.domain.chat.message;

import br.com.iolab.commons.domain.model.Model;
import br.com.iolab.commons.domain.validation.Result;
import br.com.iolab.socia.domain.chat.ChatID;
import br.com.iolab.socia.domain.chat.message.types.MessageRoleType;
import br.com.iolab.socia.domain.chat.message.types.MessageStatusType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.time.Instant;

import static br.com.iolab.commons.domain.utils.InstantUtils.now;
import static br.com.iolab.commons.types.Checks.checkNonNull;
import static br.com.iolab.commons.types.Checks.checkNotBlank;

@Getter
@ToString
public class Message extends Model<MessageID> {
    private final ChatID chatID;
    private final MessageStatusType status;
    private final MessageRoleType role;
    private final String content;

    @Builder(toBuilder = true, access = AccessLevel.PRIVATE)
    private Message (
            @NonNull final MessageID id,
            @NonNull final Instant createdAt,
            @NonNull final Instant updatedAt,
            final ChatID chatID,
            final MessageStatusType status,
            final MessageRoleType role,
            final String content
    ) {
        super(id, createdAt, updatedAt);
        this.chatID = checkNonNull(chatID, "ChatID não pode ser nulo!");
        this.status = checkNonNull(status, "Status não pode ser nulo!");
        this.role = checkNonNull(role, "Role não pode ser nulo!");
        this.content = checkNotBlank(content, "Content não pode ser vazio!");
    }

    public static Result<Message> create (
            final ChatID chatID,
            final MessageStatusType status,
            final MessageRoleType role,
            final String content
    ) {
        var now = now();
        return new Message(
                MessageID.generate(now),
                now,
                now,
                chatID,
                status,
                role,
                content
        ).validate();
    }

    public static @NonNull Message with (
            final MessageID id,
            final Instant createdAt,
            final Instant updatedAt,
            final ChatID chatID,
            final MessageStatusType status,
            final MessageRoleType role,
            final String content
    ) {
        return new Message(
                id,
                createdAt,
                updatedAt,
                chatID,
                status,
                role,
                content
        );
    }

    @Override
    protected Result<Message> validate () {
        var result = Result.builder(this);
        return result.build();
    }
}
