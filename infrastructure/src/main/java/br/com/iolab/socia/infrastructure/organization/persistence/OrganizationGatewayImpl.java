package br.com.iolab.socia.infrastructure.organization.persistence;

import br.com.iolab.commons.infrastructure.persistence.BasicModelGateway;
import br.com.iolab.socia.domain.organization.Organization;
import br.com.iolab.socia.domain.organization.OrganizationGateway;
import br.com.iolab.socia.domain.organization.OrganizationID;
import br.com.iolab.infrastructure.jooq.generated.tables.records.OrganizationsRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static br.com.iolab.infrastructure.jooq.generated.tables.Organizations.ORGANIZATIONS;

@Repository
public class OrganizationGatewayImpl extends BasicModelGateway<Organization, OrganizationID, OrganizationsRecord> implements OrganizationGateway {
    protected OrganizationGatewayImpl (
            final DSLContext readOnlyDSLContext,
            final DSLContext writeOnlyDSLContext,
            final OrganizationMapperImpl organizationMapper
    ) {
        super(
                readOnlyDSLContext,
                writeOnlyDSLContext,
                organizationMapper,
                ORGANIZATIONS,
                ORGANIZATIONS.ID,
                ORGANIZATIONS.UPDATED_AT
        );
    }
}
