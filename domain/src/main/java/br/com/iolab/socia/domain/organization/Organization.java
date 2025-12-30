package br.com.iolab.socia.domain.organization;

import br.com.iolab.commons.domain.model.Model;
import br.com.iolab.commons.domain.validation.Result;
import br.com.iolab.commons.types.fields.TaxID;
import br.com.iolab.socia.domain.organization.types.OrganizationStatusType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.time.Instant;
import java.util.Objects;

import static br.com.iolab.commons.domain.utils.InstantUtils.now;
import static br.com.iolab.commons.types.Checks.checkNonNull;
import static br.com.iolab.commons.types.Checks.checkNotBlank;
import static br.com.iolab.socia.domain.organization.types.OrganizationStatusType.ACTIVE;
import static br.com.iolab.socia.domain.organization.types.OrganizationStatusType.PROSPECT;

@Getter
@ToString
public class Organization extends Model<OrganizationID> {
    private final String name;
    private final TaxID taxID;
    private final OrganizationStatusType status;

    @Builder(toBuilder = true, access = AccessLevel.PRIVATE)
    private Organization (
            @NonNull final OrganizationID id,
            @NonNull final Instant createdAt,
            @NonNull final Instant updatedAt,
            final String name,
            final TaxID taxID,
            final OrganizationStatusType status
    ) {
        super(id, createdAt, updatedAt);
        this.name = checkNotBlank(name, "Nome da organização não pode ser vazio!");
        this.taxID = taxID;
        this.status = checkNonNull(status, "Status não pode ser nulo!");
    }

    public static Result<Organization> create (
            final String name,
            final TaxID taxID
    ) {
        var now = now();
        return new Organization(
                OrganizationID.generate(now),
                now,
                now,
                name,
                taxID,
                ACTIVE
        ).validate();
    }

    public static Organization with (
            final OrganizationID id,
            final Instant createdAt,
            final Instant updatedAt,
            final String name,
            final TaxID taxID,
            final OrganizationStatusType status
    ) {
        return new Organization(
                id,
                createdAt,
                updatedAt,
                name,
                taxID,
                status
        );
    }

    @Override
    protected Result<Organization> validate () {
        var result = Result.builder(this);

        if (!PROSPECT.equals(status) && Objects.isNull(taxID)) {
            result.appendError("Somente prospects podem ter o CPF/CNPJ vazio!");
        }

        return result.build();
    }
}
