package br.com.iolab.socia.infrastructure.assistant.persistence;

import br.com.iolab.commons.infrastructure.persistence.BasicModelGateway;
import br.com.iolab.commons.types.Streams;
import br.com.iolab.socia.domain.assistant.Assistant;
import br.com.iolab.socia.domain.assistant.AssistantGateway;
import br.com.iolab.socia.domain.assistant.AssistantID;
import br.com.iolab.socia.domain.organization.OrganizationID;
import br.com.iolab.infrastructure.jooq.generated.tables.records.AssistantsRecord;
import lombok.NonNull;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static br.com.iolab.infrastructure.jooq.generated.tables.Assistants.ASSISTANTS;

@Repository
public class AssistantGatewayImpl extends BasicModelGateway<Assistant, AssistantID, AssistantsRecord> implements AssistantGateway {
    protected AssistantGatewayImpl (
            final DSLContext readOnlyDSLContext,
            final DSLContext writeOnlyDSLContext,
            final AssistantMapperImpl assistantMapper
    ) {
        super(
                readOnlyDSLContext,
                writeOnlyDSLContext,
                assistantMapper,
                ASSISTANTS,
                ASSISTANTS.ID,
                ASSISTANTS.UPDATED_AT
        );
    }

    @Override
    public List<Assistant> findAllByOrganizationIDIn (@NonNull final Iterable<OrganizationID> organizationIDs) {
        var ids = Streams.streamOf(organizationIDs)
                .map(OrganizationID::value)
                .toList();

        return this.readOnlyDSLContext
                .selectFrom(ASSISTANTS)
                .where(ASSISTANTS.ORGANIZATION_ID.in(ids))
                .fetch()
                .map(this.mapper::toModel);
    }
}
