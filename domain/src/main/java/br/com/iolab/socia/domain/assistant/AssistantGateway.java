package br.com.iolab.socia.domain.assistant;

import br.com.iolab.commons.domain.model.ModelGateway;
import br.com.iolab.socia.domain.organization.OrganizationID;
import lombok.NonNull;

import java.util.List;

public interface AssistantGateway extends ModelGateway<Assistant, AssistantID> {
    List<Assistant> findAllByOrganizationIDIn(@NonNull Iterable<OrganizationID> organizationID);
}
