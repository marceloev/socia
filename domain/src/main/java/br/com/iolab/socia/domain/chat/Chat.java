package br.com.iolab.socia.domain.chat;

import br.com.iolab.commons.domain.model.Model;
import br.com.iolab.commons.domain.validation.Result;
import br.com.iolab.commons.types.fields.Phone;
import br.com.iolab.socia.domain.assistant.AssistantID;
import br.com.iolab.socia.domain.chat.types.ChatStatusType;
import br.com.iolab.socia.domain.organization.OrganizationID;
import br.com.iolab.socia.domain.user.UserID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.time.Instant;

import static br.com.iolab.commons.domain.utils.InstantUtils.now;
import static br.com.iolab.commons.types.Checks.checkNonNull;
import static br.com.iolab.socia.domain.chat.types.ChatStatusType.CREATED;

@Getter
@ToString
public class Chat extends Model<ChatID> {
    private final OrganizationID organizationID;
    private final AssistantID assistantID;
    private final UserID userID;
    private final Phone to;
    private final Phone from;
    private final ChatStatusType status;
    private final Long count;

    @Builder(toBuilder = true, access = AccessLevel.PRIVATE)
    private Chat (
            @NonNull final ChatID id,
            @NonNull final Instant createdAt,
            @NonNull final Instant updatedAt,
            final OrganizationID organizationID,
            final AssistantID assistantID,
            final UserID userID,
            final Phone to,
            final Phone from,
            final ChatStatusType status,
            final Long count
    ) {
        super(id, createdAt, updatedAt);
        this.organizationID = checkNonNull(organizationID, "OrganizationID não pode ser nulo!");
        this.assistantID = checkNonNull(assistantID, "AssistantID não pode ser nulo!");
        this.userID = checkNonNull(userID, "UserID não pode ser nulo!");
        this.to = checkNonNull(to, "Destinatário não pode ser nulo!");
        this.from = checkNonNull(from, "Remetente não pode ser nulo!");
        this.status = checkNonNull(status, "Status não pode ser nulo!");
        this.count = checkNonNull(count, "Count não pode ser nulo!");
    }

    public static Result<Chat> create (
            final OrganizationID organizationID,
            final AssistantID assistantID,
            final UserID userID,
            final Phone to,
            final Phone from,
            final Long count
    ) {
        var now = now();
        return new Chat(
                ChatID.generate(now),
                now,
                now,
                organizationID,
                assistantID,
                userID,
                to,
                from,
                CREATED,
                count
        ).validate();
    }

    public static @NonNull Chat with (
            final ChatID id,
            final Instant createdAt,
            final Instant updatedAt,
            final OrganizationID organizationID,
            final AssistantID assistantID,
            final UserID userID,
            final Phone to,
            final Phone from,
            final ChatStatusType status,
            final Long count
    ) {
        return new Chat(
                id,
                createdAt,
                updatedAt,
                organizationID,
                assistantID,
                userID,
                to,
                from,
                status,
                count
        );
    }

    @Override
    protected Result<Chat> validate () {
        var result = Result.builder(this);
        return result.build();
    }

    public Result<Chat> incrementMessageCount () {
        return this.toBuilder()
                .count(getCount() + 1)
                .build()
                .validate();
    }
}
