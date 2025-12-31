package br.com.iolab.socia.infrastructure.chat.message.persistence;

import br.com.iolab.commons.domain.model.ModelID;
import br.com.iolab.commons.domain.model.ModelMapper;
import br.com.iolab.infrastructure.jooq.generated.tables.records.MessagesRecord;
import br.com.iolab.socia.domain.chat.ChatID;
import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.MessageID;
import br.com.iolab.socia.domain.chat.message.types.MessageContent;
import br.com.iolab.socia.domain.chat.message.types.MessageRoleType;
import br.com.iolab.socia.domain.chat.message.types.MessageStatusType;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import static br.com.iolab.commons.types.Optionals.mapNullable;

@Service
public class MessageMapperImpl extends ModelMapper<Message, MessagesRecord> {
    @Override
    public @NonNull MessagesRecord fromModel (@NonNull final Message message) {
        return new MessagesRecord(
                mapNullable(message.getId(), ModelID::value),
                message.getCreatedAt(),
                message.getCreatedAt(),
                mapNullable(message.getChatID(), ModelID::value),
                mapNullable(message.getStatus(), Enum::name),
                mapNullable(message.getRole(), Enum::name),
                mapNullable(message.getContent(), MessageContent::value),
                message.getNextCheckTime()
        );
    }

    @Override
    public @NonNull Message toModel (@NonNull final MessagesRecord messagesRecord) {
        return Message.with(
                mapNullable(messagesRecord.getId(), MessageID::from),
                messagesRecord.getCreatedAt(),
                messagesRecord.getUpdatedAt(),
                mapNullable(messagesRecord.getChatId(), ChatID::from),
                mapNullable(messagesRecord.getStatus(), MessageStatusType::valueOf),
                mapNullable(messagesRecord.getRole(), MessageRoleType::valueOf),
                mapNullable(messagesRecord.getContent(), MessageContent::of),
                messagesRecord.getNextCheckTime()
        );
    }
}
