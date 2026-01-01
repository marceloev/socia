package br.com.iolab.socia.infrastructure.chat.message.resource.persistence;

import br.com.iolab.commons.domain.model.ModelID;
import br.com.iolab.commons.domain.model.ModelMapper;
import br.com.iolab.infrastructure.jooq.generated.tables.records.MessageResourcesRecord;
import br.com.iolab.socia.domain.chat.message.MessageID;
import br.com.iolab.socia.domain.chat.message.resource.MessageResource;
import br.com.iolab.socia.domain.chat.message.resource.MessageResourceID;
import br.com.iolab.socia.domain.chat.message.resource.types.MessageResourceType;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import static br.com.iolab.commons.types.Optionals.mapNullable;

@Service
public class MessageResourceMapperImpl extends ModelMapper<MessageResource, MessageResourcesRecord> {
    @Override
    public @NonNull MessageResourcesRecord fromModel (@NonNull final MessageResource messageResource) {
        return new MessageResourcesRecord(
                mapNullable(messageResource.getId(), ModelID::value),
                messageResource.getCreatedAt(),
                messageResource.getUpdatedAt(),
                mapNullable(messageResource.getMessageID(), ModelID::value),
                mapNullable(messageResource.getType(), Enum::name),
                messageResource.getContentType(),
                messageResource.getFile()
        );
    }

    @Override
    public @NonNull MessageResource toModel (@NonNull final MessageResourcesRecord record) {
        return MessageResource.with(
                mapNullable(record.getId(), MessageResourceID::from),
                record.getCreatedAt(),
                record.getUpdatedAt(),
                mapNullable(record.getMessageId(), MessageID::from),
                mapNullable(record.getType(), MessageResourceType::valueOf),
                record.getContenttype(),
                record.getFile()
        );
    }
}
