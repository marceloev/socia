package br.com.iolab.socia.domain.organization;

import br.com.iolab.commons.domain.model.ModelGateway;
import br.com.iolab.socia.domain.user.UserID;
import lombok.NonNull;

import java.util.Optional;

public interface OrganizationGateway extends ModelGateway<Organization, OrganizationID> {
    Optional<Organization> findMainByUserID(@NonNull UserID userID);
}
