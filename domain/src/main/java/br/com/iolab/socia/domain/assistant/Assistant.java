package br.com.iolab.socia.domain.assistant;

import br.com.iolab.commons.domain.model.Model;
import br.com.iolab.commons.domain.validation.Result;
import br.com.iolab.commons.types.fields.Phone;
import br.com.iolab.socia.domain.assistant.types.AssistantProviderType;
import br.com.iolab.socia.domain.assistant.types.AssistantStatusType;
import br.com.iolab.socia.domain.organization.OrganizationID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.time.Instant;

import static br.com.iolab.commons.domain.utils.InstantUtils.now;
import static br.com.iolab.commons.types.Checks.checkNonNull;
import static br.com.iolab.commons.types.Checks.checkNotBlank;
import static br.com.iolab.socia.domain.assistant.types.AssistantStatusType.ACTIVE;

@Getter
@ToString
public class Assistant extends Model<AssistantID> {
    private final OrganizationID organizationID;
    private final AssistantStatusType status;
    private final Phone phone;
    private final AssistantProviderType provider;
    private final String version;
    private final String prompt;

    @Builder(toBuilder = true, access = AccessLevel.PRIVATE)
    private Assistant (
            @NonNull final AssistantID id,
            @NonNull final Instant createdAt,
            @NonNull final Instant updatedAt,
            final OrganizationID organizationID,
            final AssistantStatusType status,
            final Phone phone,
            final AssistantProviderType provider,
            final String version,
            final String prompt
    ) {
        super(id, createdAt, updatedAt);
        this.organizationID = checkNonNull(organizationID, "OrganizationID não pode ser nulo!");
        this.status = checkNonNull(status, "Status não pode ser nulo!");
        this.phone = checkNonNull(phone, "Phone não pode ser nulo!");
        this.provider = checkNonNull(provider, "Provider não pode ser nulo!");
        this.version = checkNotBlank(version, "Version não pode ser vazia!");
        this.prompt = checkNotBlank(prompt, "Prompt não pode ser vazio!");
    }

    public static Result<Assistant> create (
            final OrganizationID organizationID,
            final Phone phone,
            final AssistantProviderType provider,
            final String version,
            final String prompt
    ) {
        var now = now();
        return new Assistant(
                AssistantID.generate(now),
                now,
                now,
                organizationID,
                ACTIVE,
                phone,
                provider,
                version,
                prompt
        ).validate();
    }

    public static Result<Assistant> suggest (
            final OrganizationID organizationID,
            final Phone phone
    ) {
        return create(
                organizationID,
                phone,
                AssistantProviderType.GEMINI,
                "gemini-3-flash-preview",
                "Feliz"
        );
    }

    public static @NonNull Assistant with (
            final AssistantID id,
            final Instant createdAt,
            final Instant updatedAt,
            final OrganizationID organizationID,
            final AssistantStatusType status,
            final Phone phone,
            final AssistantProviderType provider,
            final String version,
            final String prompt
    ) {
        return new Assistant(
                id,
                createdAt,
                updatedAt,
                organizationID,
                status,
                phone,
                provider,
                version,
                prompt
        );
    }

    @Override
    protected Result<Assistant> validate () {
        var result = Result.builder(this);
        return result.build();
    }
}
