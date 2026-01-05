package br.com.iolab.socia.infrastructure.organization.persistence;

import br.com.iolab.commons.infrastructure.persistence.BasicModelGateway;
import br.com.iolab.infrastructure.jooq.generated.tables.records.OrganizationsRecord;
import br.com.iolab.socia.domain.member.types.MemberRoleType;
import br.com.iolab.socia.domain.organization.Organization;
import br.com.iolab.socia.domain.organization.OrganizationGateway;
import br.com.iolab.socia.domain.organization.OrganizationID;
import br.com.iolab.socia.domain.user.UserID;
import lombok.NonNull;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static br.com.iolab.infrastructure.jooq.generated.tables.Members.MEMBERS;
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

    @Override
    public Optional<Organization> findMainByUserID (@NonNull final UserID userID) {
        return this.readOnlyDSLContext
                .select(ORGANIZATIONS.asterisk())
                .from(ORGANIZATIONS)
                .innerJoin(MEMBERS).on(MEMBERS.ORGANIZATION_ID.eq(ORGANIZATIONS.ID))
                .where(MEMBERS.USER_ID.eq(userID.value()))
                .and(MEMBERS.ROLE.eq(MemberRoleType.OWNER.name()))
                .limit(1)
                .fetchOptionalInto(OrganizationsRecord.class)
                .map(this.mapper::toModel);
    }
}
