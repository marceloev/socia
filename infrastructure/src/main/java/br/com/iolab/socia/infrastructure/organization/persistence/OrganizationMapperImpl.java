package br.com.iolab.socia.infrastructure.organization.persistence;

import br.com.iolab.commons.domain.model.ModelID;
import br.com.iolab.commons.domain.model.ModelMapper;
import br.com.iolab.commons.types.fields.TaxID;
import br.com.iolab.socia.domain.organization.Organization;
import br.com.iolab.socia.domain.organization.OrganizationID;
import br.com.iolab.socia.domain.organization.types.OrganizationStatusType;
import br.com.iolab.infrastructure.jooq.generated.tables.records.OrganizationsRecord;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import static br.com.iolab.commons.types.Optionals.mapNullable;

@Service
public class OrganizationMapperImpl extends ModelMapper<Organization, OrganizationsRecord> {
    @Override
    public @NonNull OrganizationsRecord fromModel (@NonNull final Organization organization) {
        return new OrganizationsRecord(
                mapNullable(organization.getId(), ModelID::value),
                organization.getCreatedAt(),
                organization.getUpdatedAt(),
                organization.getName(),
                mapNullable(organization.getTaxID(), TaxID::value),
                mapNullable(organization.getStatus(), Enum::name)
        );
    }

    @Override
    public @NonNull Organization toModel (@NonNull final OrganizationsRecord record) {
        return Organization.with(
                mapNullable(record.getId(), OrganizationID::from),
                record.getCreatedAt(),
                record.getUpdatedAt(),
                record.getName(),
                mapNullable(record.getTaxId(), TaxID::of),
                mapNullable(record.getStatus(), OrganizationStatusType::valueOf)
        );
    }
}
