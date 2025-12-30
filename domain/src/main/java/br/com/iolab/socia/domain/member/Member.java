package br.com.iolab.socia.domain.member;

import br.com.iolab.commons.domain.model.Model;
import br.com.iolab.commons.domain.validation.Result;
import br.com.iolab.socia.domain.member.types.MemberRoleType;
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

@Getter
@ToString
public class Member extends Model<MemberID> {
    private final OrganizationID organizationID;
    private final UserID userID;
    private final MemberRoleType role;

    @Builder(toBuilder = true, access = AccessLevel.PRIVATE)
    private Member (
            @NonNull final MemberID id,
            @NonNull final Instant createdAt,
            @NonNull final Instant updatedAt,
            final OrganizationID organizationID,
            final UserID userID,
            final MemberRoleType role
    ) {
        super(id, createdAt, updatedAt);
        this.organizationID = checkNonNull(organizationID, "OrganizationID não pode ser nulo!");
        this.userID = checkNonNull(userID, "UserID não pode ser nulo!");
        this.role = checkNonNull(role, "Role não pode ser nulo!");
    }

    public static Result<Member> create (
            final OrganizationID organizationID,
            final UserID userID,
            final MemberRoleType role
    ) {
        var now = now();
        return new Member(
                MemberID.generate(now),
                now,
                now,
                organizationID,
                userID,
                role
        ).validate();
    }

    public static @NonNull Member with (
            final MemberID id,
            final Instant createdAt,
            final Instant updatedAt,
            final OrganizationID organizationID,
            final UserID userID,
            final MemberRoleType role
    ) {
        return new Member(
                id,
                createdAt,
                updatedAt,
                organizationID,
                userID,
                role
        );
    }

    @Override
    protected Result<Member> validate () {
        var result = Result.builder(this);
        return result.build();
    }
}
