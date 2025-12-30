package br.com.iolab.socia.infrastructure.chat.persistence;

import br.com.iolab.commons.domain.model.ModelID;
import br.com.iolab.commons.domain.model.ModelMapper;
import br.com.iolab.commons.types.fields.Phone;
import br.com.iolab.socia.domain.assistant.AssistantID;
import br.com.iolab.socia.domain.chat.Chat;
import br.com.iolab.socia.domain.chat.ChatID;
import br.com.iolab.socia.domain.chat.types.ChatStatusType;
import br.com.iolab.socia.domain.organization.OrganizationID;
import br.com.iolab.socia.domain.user.UserID;
import br.com.iolab.infrastructure.jooq.generated.tables.records.ChatsRecord;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import static br.com.iolab.commons.types.Optionals.mapNullable;

@Service
public class ChatMapperImpl extends ModelMapper<Chat, ChatsRecord> {
    @Override
    public @NonNull ChatsRecord fromModel (@NonNull final Chat chat) {
        return new ChatsRecord(
                mapNullable(chat.getId(), ModelID::value),
                chat.getCreatedAt(),
                chat.getUpdatedAt(),
                mapNullable(chat.getOrganizationID(), ModelID::value),
                mapNullable(chat.getAssistantID(), ModelID::value),
                mapNullable(chat.getUserID(), ModelID::value),
                mapNullable(chat.getTo(), Phone::value),
                mapNullable(chat.getFrom(), Phone::value),
                mapNullable(chat.getStatus(), Enum::name),
                chat.getCount()
        );
    }

    @Override
    public @NonNull Chat toModel (@NonNull final ChatsRecord record) {
        return Chat.with(
                mapNullable(record.getId(), ChatID::from),
                record.getCreatedAt(),
                record.getUpdatedAt(),
                mapNullable(record.getOrganizationId(), OrganizationID::from),
                mapNullable(record.getAssistantId(), AssistantID::from),
                mapNullable(record.getUserId(), UserID::from),
                mapNullable(record.getPhoneTo(), Phone::of),
                mapNullable(record.getPhoneFrom(), Phone::of),
                mapNullable(record.getStatus(), ChatStatusType::valueOf),
                record.getCount()
        );
    }
}
