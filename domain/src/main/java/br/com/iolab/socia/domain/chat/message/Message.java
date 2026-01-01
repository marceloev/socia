package br.com.iolab.socia.domain.chat.message;

import br.com.iolab.commons.domain.model.Model;
import br.com.iolab.commons.domain.validation.Result;
import br.com.iolab.socia.domain.chat.ChatID;
import br.com.iolab.socia.domain.chat.message.types.MessageContent;
import br.com.iolab.socia.domain.chat.message.types.MessageRoleType;
import br.com.iolab.socia.domain.chat.message.types.MessageStatusType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.time.Instant;
import java.util.Map;

import static br.com.iolab.commons.domain.utils.InstantUtils.now;
import static br.com.iolab.commons.types.Checks.checkNonNull;
import static br.com.iolab.socia.domain.chat.message.types.MessageStatusType.*;

@Getter
@ToString
public class Message extends Model<MessageID> {
    private final ChatID chatID;
    private final MessageStatusType status;
    private final MessageRoleType role;
    private final MessageContent content;
    private final Map<String, String> metadata;
    private final Instant nextCheckTime;

    @Builder(toBuilder = true, access = AccessLevel.PRIVATE)
    private Message (
            @NonNull final MessageID id,
            @NonNull final Instant createdAt,
            @NonNull final Instant updatedAt,
            final ChatID chatID,
            final MessageStatusType status,
            final MessageRoleType role,
            final MessageContent content,
            final Map<String, String> metadata,
            final Instant nextCheckTime
    ) {
        super(id, createdAt, updatedAt);
        this.chatID = checkNonNull(chatID, "ChatID não pode ser nulo!");
        this.status = checkNonNull(status, "Status não pode ser nulo!");
        this.role = checkNonNull(role, "Role não pode ser nulo!");
        this.content = checkNonNull(content, "Content não pode ser vazio!");
        this.metadata = checkNonNull(metadata, "Metadata não pode ser nulo!");
        this.nextCheckTime = checkNonNull(nextCheckTime, "Tempo de checagem não pode ser vazio!");
    }

    public static Result<Message> create (
            final ChatID chatID,
            final MessageStatusType status,
            final MessageRoleType role,
            final MessageContent content,
            final Map<String, String> metadata
    ) {
        var now = now();
        return new Message(
                MessageID.generate(now),
                now,
                now,
                chatID,
                status,
                role,
                content,
                metadata,
                now
        ).validate();
    }

    public static @NonNull Message with (
            final MessageID id,
            final Instant createdAt,
            final Instant updatedAt,
            final ChatID chatID,
            final MessageStatusType status,
            final MessageRoleType role,
            final MessageContent content,
            final Map<String, String> metadata,
            final Instant nextCheckTime
    ) {
        return new Message(
                id,
                createdAt,
                updatedAt,
                chatID,
                status,
                role,
                content,
                metadata,
                nextCheckTime
        );
    }

    @Override
    protected Result<Message> validate () {
        var result = Result.builder(this);
        return result.build();
    }

    public Result<Message> markAsProcessed () {
        return this.toBuilder()
                .status(PROCESSED)
                .nextCheckTime(now())
                .build()
                .validate();
    }

    public Result<Message> markAsFailed () {
        return this.toBuilder()
                .status(FAILED)
                .nextCheckTime(now())
                .build()
                .validate();
    }

    public Result<Message> markAsSent () {
        return this.toBuilder()
                .status(SENT)
                .build()
                .validate();
    }
}
