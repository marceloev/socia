package br.com.iolab.socia.infrastructure.chat.message.persistence;

import br.com.iolab.commons.domain.pagination.Pagination;
import br.com.iolab.commons.infrastructure.persistence.BasicModelGateway;
import br.com.iolab.infrastructure.jooq.generated.tables.records.MessagesRecord;
import br.com.iolab.socia.domain.chat.ChatID;
import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.MessageGateway;
import br.com.iolab.socia.domain.chat.message.MessageID;
import br.com.iolab.socia.domain.chat.message.MessageSearchQuery;
import br.com.iolab.socia.domain.chat.message.types.MessageStatusType;
import lombok.NonNull;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static br.com.iolab.infrastructure.jooq.generated.tables.Messages.MESSAGES;
import static java.util.Objects.nonNull;
import static org.jooq.impl.DSL.*;

@Repository
public class MessageGatewayImpl extends BasicModelGateway<Message, MessageID, MessagesRecord> implements MessageGateway {
    protected MessageGatewayImpl (
            final DSLContext readOnlyDSLContext,
            final DSLContext writeOnlyDSLContext,
            final MessageMapperImpl messageMapper
    ) {
        super(
                readOnlyDSLContext,
                writeOnlyDSLContext,
                messageMapper,
                MESSAGES,
                MESSAGES.ID,
                MESSAGES.UPDATED_AT
        );
    }

    @Override
    public List<Message> reserve (
            @NonNull final Integer size,
            @NonNull final Instant now,
            @NonNull final Instant lease
    ) {
        var targetChats = name("target_chats").as(
                select(MESSAGES.CHAT_ID)
                        .from(MESSAGES)
                        .where(MESSAGES.STATUS.equal(MessageStatusType.RECEIVED.name()))
                        .and(MESSAGES.NEXT_CHECK_TIME.lessOrEqual(now))
                        .groupBy(MESSAGES.CHAT_ID)
                        .orderBy(min(MESSAGES.NEXT_CHECK_TIME).asc())
                        .limit(size)
                        .forUpdate()
                        .skipLocked()
        );

        // CTE 2: As 500 Mensagens desses chats
        var targetMessages = name("target_messages").as(
                select(MESSAGES.ID)
                        .from(MESSAGES)
                        .join(targetChats).on(MESSAGES.CHAT_ID.equal(targetChats.field(MESSAGES.CHAT_ID)))
                        .where(MESSAGES.STATUS.equal(MessageStatusType.RECEIVED.name()))
                        .orderBy(MESSAGES.CHAT_ID, MESSAGES.NEXT_CHECK_TIME.asc())
                        .limit(500)
        );

        // Execução do Update com Returning
        return this.readOnlyDSLContext.with(targetChats)
                .with(targetMessages)
                .update(MESSAGES)
                .set(MESSAGES.NEXT_CHECK_TIME, lease)
                .where(MESSAGES.ID.in(
                        select(field(name("target_messages", "id"), UUID.class))
                                .from(targetMessages)
                ))
                .returning()
                .fetch()
                .map(this.mapper::toModel);
    }

    @Override
    public List<Message> findAllByChatID (@NonNull final ChatID chatID) {
        return this.readOnlyDSLContext
                .selectFrom(MESSAGES)
                .where(MESSAGES.CHAT_ID.eq(chatID.value()))
                .orderBy(MESSAGES.CREATED_AT.asc())
                .fetch()
                .map(this.mapper::toModel);
    }

    @Override
    public Pagination<Message> findAll (@NonNull final MessageSearchQuery query) {
        var result = this.readOnlyDSLContext.selectFrom(table)
                .where(nonNull(query.statuses()) ? MESSAGES.STATUS.in(query.statuses()) : DSL.noCondition())
                .orderBy(MESSAGES.field(query.sort().name()).asc())
                .limit(query.perPage())
                .offset(query.page() * query.perPage())
                .fetch();

        return Pagination.with(
                query.page(),
                query.perPage(),
                -1,
                result.stream().map(this.mapper::toModel).toList()
        );
    }
}
