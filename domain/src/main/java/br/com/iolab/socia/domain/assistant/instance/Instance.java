package br.com.iolab.socia.domain.assistant.instance;

import br.com.iolab.commons.domain.model.Model;
import br.com.iolab.commons.domain.utils.InstantUtils;
import br.com.iolab.commons.domain.validation.Result;
import br.com.iolab.socia.domain.assistant.AssistantID;
import br.com.iolab.socia.domain.assistant.instance.types.InstanceOriginType;
import br.com.iolab.socia.domain.assistant.instance.types.InstanceStatusType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.time.Instant;
import java.util.Objects;

import static br.com.iolab.commons.types.Checks.checkNonNull;
import static br.com.iolab.commons.types.Checks.checkNotBlank;

@Getter
@ToString
public class Instance extends Model<InstanceID> {
    private final AssistantID assistantID;
    private final boolean showcase;
    private final InstanceOriginType origin;
    private final String account;
    private final InstanceStatusType status;

    @Builder(toBuilder = true, access = AccessLevel.PRIVATE)
    private Instance (
            @NonNull final InstanceID id,
            @NonNull final Instant createdAt,
            @NonNull final Instant updatedAt,
            final AssistantID assistantID,
            final boolean showcase,
            final InstanceOriginType origin,
            final String account,
            final InstanceStatusType status
    ) {
        super(id, createdAt, updatedAt);
        this.assistantID = assistantID;
        this.showcase = showcase;
        this.origin = checkNonNull(origin, "Origem da instância não pode ser vazia!");
        this.account = checkNotBlank(account, "Conta da instancia não pode ser vazia!");
        this.status = checkNonNull(status, "Status da instância não pode ser vazia!");
    }

    public static Instance with (
            final InstanceID id,
            final Instant createdAt,
            final Instant updatedAt,
            final AssistantID assistantID,
            final boolean showcase,
            final InstanceOriginType origin,
            final String account,
            final InstanceStatusType status
    ) {
        return new Instance(
                id,
                createdAt,
                updatedAt,
                assistantID,
                showcase,
                origin,
                account,
                status
        );
    }

    public static Result<Instance> create (
            final AssistantID assistantID,
            final boolean showcase,
            final InstanceOriginType origin,
            final String account,
            final InstanceStatusType status
    ) {
        var now = InstantUtils.now();
        return new Instance(
                InstanceID.generate(now),
                now,
                now,
                assistantID,
                showcase,
                origin,
                account,
                status
        ).validate();
    }

    public Result<Instance> validate () {
        var result = Result.builder(this);

        if (Objects.isNull(assistantID) && !showcase) {
            result.appendError("Somente instancias vitrines estão isentas de ter um assistente!");
        }

        return result.build();
    }
}
