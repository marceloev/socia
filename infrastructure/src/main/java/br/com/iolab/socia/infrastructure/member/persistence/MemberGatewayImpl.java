package br.com.iolab.socia.infrastructure.member.persistence;

import br.com.iolab.commons.infrastructure.persistence.BasicModelGateway;
import br.com.iolab.socia.domain.member.Member;
import br.com.iolab.socia.domain.member.MemberGateway;
import br.com.iolab.socia.domain.member.MemberID;
import br.com.iolab.socia.domain.organization.OrganizationID;
import br.com.iolab.socia.domain.user.UserID;
import br.com.iolab.infrastructure.jooq.generated.tables.records.MembersRecord;
import lombok.NonNull;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static br.com.iolab.infrastructure.jooq.generated.tables.Members.MEMBERS;

@Repository
public class MemberGatewayImpl extends BasicModelGateway<Member, MemberID, MembersRecord> implements MemberGateway {
    protected MemberGatewayImpl (
            final DSLContext readOnlyDSLContext,
            final DSLContext writeOnlyDSLContext,
            final MemberMapperImpl memberMapper
    ) {
        super(
                readOnlyDSLContext,
                writeOnlyDSLContext,
                memberMapper,
                MEMBERS,
                MEMBERS.ID,
                MEMBERS.UPDATED_AT
        );
    }

    @Override
    public List<Member> findAllByOrganizationID (@NonNull final OrganizationID organizationID) {
        return this.readOnlyDSLContext
                .selectFrom(MEMBERS)
                .where(MEMBERS.ORGANIZATION_ID.eq(organizationID.value()))
                .fetch()
                .map(this.mapper::toModel);
    }

    @Override
    public List<Member> findAllByUserID (@NonNull final UserID userID) {
        return this.readOnlyDSLContext
                .selectFrom(MEMBERS)
                .where(MEMBERS.USER_ID.eq(userID.value()))
                .fetch()
                .map(this.mapper::toModel);
    }
}
