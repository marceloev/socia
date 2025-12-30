package br.com.iolab.socia.infrastructure.chat.persistence;

import br.com.iolab.commons.infrastructure.persistence.BasicModelGateway;
import br.com.iolab.commons.types.fields.Phone;
import br.com.iolab.socia.domain.chat.Chat;
import br.com.iolab.socia.domain.chat.ChatGateway;
import br.com.iolab.socia.domain.chat.ChatID;
import br.com.iolab.infrastructure.jooq.generated.tables.records.ChatsRecord;
import lombok.NonNull;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static br.com.iolab.infrastructure.jooq.generated.tables.Chats.CHATS;

@Repository
public class ChatGatewayImpl extends BasicModelGateway<Chat, ChatID, ChatsRecord> implements ChatGateway {
    protected ChatGatewayImpl (
            final DSLContext readOnlyDSLContext,
            final DSLContext writeOnlyDSLContext,
            final ChatMapperImpl chatMapper
    ) {
        super(
                readOnlyDSLContext,
                writeOnlyDSLContext,
                chatMapper,
                CHATS,
                CHATS.ID,
                CHATS.UPDATED_AT
        );
    }

    @Override
    public Optional<Chat> findByToAndFrom (@NonNull final Phone to, @NonNull final Phone from) {
        return this.readOnlyDSLContext
                .selectFrom(CHATS)
                .where(CHATS.PHONE_TO.eq(to.value()))
                .and(CHATS.PHONE_FROM.eq(from.value()))
                .fetchOptional()
                .map(this.mapper::toModel);
    }
}
