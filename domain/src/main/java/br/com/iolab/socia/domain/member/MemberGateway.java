package br.com.iolab.socia.domain.member;

import br.com.iolab.commons.domain.model.ModelGateway;
import br.com.iolab.socia.domain.organization.OrganizationID;
import br.com.iolab.socia.domain.user.UserID;
import lombok.NonNull;

import java.util.List;

public interface MemberGateway extends ModelGateway<Member, MemberID> {
    List<Member> findAllByOrganizationID(@NonNull OrganizationID organizationID);
    List<Member> findAllByUserID(@NonNull UserID userID);
}
