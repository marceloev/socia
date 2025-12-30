package br.com.iolab.socia.infrastructure.member.persistence;

import br.com.iolab.commons.domain.model.ModelID;
import br.com.iolab.commons.domain.model.ModelMapper;
import br.com.iolab.socia.domain.member.Member;
import br.com.iolab.socia.domain.member.MemberID;
import br.com.iolab.socia.domain.member.types.MemberRoleType;
import br.com.iolab.socia.domain.organization.OrganizationID;
import br.com.iolab.socia.domain.user.UserID;
import br.com.iolab.infrastructure.jooq.generated.tables.records.MembersRecord;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import static br.com.iolab.commons.types.Optionals.mapNullable;

@Service
public class MemberMapperImpl extends ModelMapper<Member, MembersRecord> {
    @Override
    public @NonNull MembersRecord fromModel (@NonNull final Member member) {
        return new MembersRecord(
                mapNullable(member.getId(), ModelID::value),
                member.getCreatedAt(),
                member.getUpdatedAt(),
                mapNullable(member.getOrganizationID(), ModelID::value),
                mapNullable(member.getUserID(), ModelID::value),
                mapNullable(member.getRole(), Enum::name)
        );
    }

    @Override
    public @NonNull Member toModel (@NonNull final MembersRecord record) {
        return Member.with(
                mapNullable(record.getId(), MemberID::from),
                record.getCreatedAt(),
                record.getUpdatedAt(),
                mapNullable(record.getOrganizationId(), OrganizationID::from),
                mapNullable(record.getUserId(), UserID::from),
                mapNullable(record.getRole(), MemberRoleType::valueOf)
        );
    }
}
