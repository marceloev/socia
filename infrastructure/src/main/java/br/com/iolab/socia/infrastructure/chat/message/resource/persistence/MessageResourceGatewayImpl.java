package br.com.iolab.socia.infrastructure.chat.message.resource.persistence;

import br.com.iolab.commons.infrastructure.persistence.BasicModelGateway;
import br.com.iolab.infrastructure.jooq.generated.tables.records.MessageResourcesRecord;
import br.com.iolab.socia.domain.chat.message.MessageID;
import br.com.iolab.socia.domain.chat.message.resource.MessageResource;
import br.com.iolab.socia.domain.chat.message.resource.MessageResourceGateway;
import br.com.iolab.socia.domain.chat.message.resource.MessageResourceID;
import lombok.NonNull;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static br.com.iolab.infrastructure.jooq.generated.tables.MessageResources.MESSAGE_RESOURCES;

@Repository
public class MessageResourceGatewayImpl extends BasicModelGateway<MessageResource, MessageResourceID, MessageResourcesRecord> implements MessageResourceGateway {
    protected MessageResourceGatewayImpl (
            final DSLContext readOnlyDSLContext,
            final DSLContext writeOnlyDSLContext,
            final MessageResourceMapperImpl messageResourceMapper
    ) {
        super(
                readOnlyDSLContext,
                writeOnlyDSLContext,
                messageResourceMapper,
                MESSAGE_RESOURCES,
                MESSAGE_RESOURCES.ID,
                MESSAGE_RESOURCES.UPDATED_AT
        );
    }

    @Override
    public List<MessageResource> findAllByIdIn (@NonNull final Set<MessageResourceID> ids) {
        return this.readOnlyDSLContext
                .selectFrom(MESSAGE_RESOURCES)
                .where(MESSAGE_RESOURCES.ID.in(ids.stream().map(MessageResourceID::value).toList()))
                .fetch()
                .map(this.mapper::toModel);
    }

    @Override
    public List<MessageResource> findAllByMessageIdIn (@NonNull Set<MessageID> ids) {
        return this.readOnlyDSLContext
                .selectFrom(MESSAGE_RESOURCES)
                .where(MESSAGE_RESOURCES.MESSAGE_ID.in(ids.stream().map(MessageID::value).toList()))
                .fetch()
                .map(this.mapper::toModel);
    }
}
