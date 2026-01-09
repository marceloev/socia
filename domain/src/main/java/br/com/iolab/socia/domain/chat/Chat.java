package br.com.iolab.socia.domain.chat;

import br.com.iolab.commons.domain.model.Model;
import br.com.iolab.commons.domain.validation.Result;
import br.com.iolab.socia.domain.assistant.AssistantID;
import br.com.iolab.socia.domain.assistant.instance.InstanceID;
import br.com.iolab.socia.domain.chat.fields.ChatAccount;
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
    private final InstanceID instanceID;
    private final UserID userID;
    private final ChatAccount account;
    private final ChatStatusType status;
    private final Long count;

    @Builder(toBuilder = true, access = AccessLevel.PRIVATE)
    private Chat (
            @NonNull final ChatID id,
            @NonNull final Instant createdAt,
            @NonNull final Instant updatedAt,
            final OrganizationID organizationID,
            final AssistantID assistantID,
            final InstanceID instanceID,
            final UserID userID,
            final ChatAccount account,
            final ChatStatusType status,
            final Long count
    ) {
        super(id, createdAt, updatedAt);
        this.organizationID = checkNonNull(organizationID, "OrganizationID não pode ser nulo!");
        this.assistantID = checkNonNull(assistantID, "AssistantID não pode ser nulo!");
        this.instanceID = checkNonNull(instanceID, "InstanceID não pode ser nulo!");
        this.userID = checkNonNull(userID, "UserID não pode ser nulo!");
        this.account = checkNonNull(account, "Conta não pode ser nulo!");
        this.status = checkNonNull(status, "Status não pode ser nulo!");
        this.count = checkNonNull(count, "Count não pode ser nulo!");
    }

    public static Result<Chat> create (
            final OrganizationID organizationID,
            final AssistantID assistantID,
            final InstanceID instanceID,
            final UserID userID,
            final ChatAccount account,
            final Long count
    ) {
        var now = now();
        return new Chat(
                ChatID.generate(now),
                now,
                now,
                organizationID,
                assistantID,
                instanceID,
                userID,
                account,
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
            final InstanceID instanceID,
            final UserID userID,
            final ChatAccount account,
            final ChatStatusType status,
            final Long count
    ) {
        return new Chat(
                id,
                createdAt,
                updatedAt,
                organizationID,
                assistantID,
                instanceID,
                userID,
                account,
                status,
                count
        );
    }

    @Override
    protected Result<Chat> validate () {
        var result = Result.builder(this);
        return result.build();
    }

    public Result<Chat> incrementMessageCount (int count) {
        return this.toBuilder()
                .count(getCount() + count)
                .build()
                .validate();
    }
}
