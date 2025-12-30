package br.com.iolab.socia.domain.chat.message.resource;

import br.com.iolab.commons.domain.model.Model;
import br.com.iolab.commons.domain.validation.Result;
import br.com.iolab.socia.domain.chat.message.MessageID;
import br.com.iolab.socia.domain.chat.message.resource.types.MessageResourceType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.time.Instant;

import static br.com.iolab.commons.domain.utils.InstantUtils.now;
import static br.com.iolab.commons.types.Checks.checkNonNull;

@Getter
@ToString
public class MessageResource extends Model<MessageResourceID> {
    private final MessageID messageID;
    private final MessageResourceType type;
    private final byte[] file;

    @Builder(toBuilder = true, access = AccessLevel.PRIVATE)
    private MessageResource (
            @NonNull final MessageResourceID id,
            @NonNull final Instant createdAt,
            @NonNull final Instant updatedAt,
            final MessageID messageID,
            final MessageResourceType type,
            final byte[] file
    ) {
        super(id, createdAt, updatedAt);
        this.messageID = checkNonNull(messageID, "MessageID não pode ser nulo!");
        this.type = checkNonNull(type, "Tipo não pode ser nulo!");
        this.file = checkNonNull(file, "Conteudo não pode ser nulo!");
    }

    public static Result<MessageResource> create (
            final MessageID messageID,
            final MessageResourceType type,
            final byte[] file
    ) {
        var now = now();
        return new MessageResource(
                MessageResourceID.generate(now),
                now,
                now,
                messageID,
                type,
                file
        ).validate();
    }

    public static @NonNull MessageResource with (
            final MessageResourceID id,
            final Instant createdAt,
            final Instant updatedAt,
            final MessageID messageID,
            final MessageResourceType type,
            final byte[] file
    ) {
        return new MessageResource(
                id,
                createdAt,
                updatedAt,
                messageID,
                type,
                file
        );
    }

    @Override
    protected Result<MessageResource> validate () {
        var result = Result.builder(this);
        return result.build();
    }
}
