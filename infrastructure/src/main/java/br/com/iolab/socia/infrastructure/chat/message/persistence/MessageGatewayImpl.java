package br.com.iolab.socia.infrastructure.chat.message.persistence;

import br.com.iolab.commons.domain.pagination.Pagination;
import br.com.iolab.commons.infrastructure.persistence.BasicModelGateway;
import br.com.iolab.infrastructure.jooq.generated.tables.records.MessagesRecord;
import br.com.iolab.socia.domain.chat.ChatID;
import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.MessageGateway;
import br.com.iolab.socia.domain.chat.message.MessageID;
import br.com.iolab.socia.domain.chat.message.MessageSearchQuery;
import br.com.iolab.socia.domain.chat.message.valueobject.ReservationPolicy;
import lombok.NonNull;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
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
    public List<Message> reserve (@NonNull final ReservationPolicy reservationPolicy) {
        /*
          WITH candidate_chats AS (
            SELECT chat_id FROM messages
            WHERE status = 'RECEIVED' AND next_check_time <= now
            GROUP BY chat_id
            ORDER BY MIN(next_check_time) ASC
            LIMIT 20
          ),
          message_leaders AS (
            SELECT chat_id, id as leader_id,
                   ROW_NUMBER() OVER (PARTITION BY chat_id ORDER BY next_check_time ASC) as rn
            FROM messages
            JOIN candidate_chats USING (chat_id)
            WHERE status = 'RECEIVED'
          ),
          locked_leaders AS (
            SELECT leader_id as id, chat_id
            FROM message_leaders
            WHERE rn = 1
            ORDER BY chat_id
            LIMIT 10
            FOR UPDATE SKIP LOCKED
          ),
          target_messages AS (
            SELECT id FROM messages
            WHERE chat_id IN (SELECT chat_id FROM locked_leaders)
              AND status = 'RECEIVED'
            ORDER BY chat_id, next_check_time ASC
            LIMIT 500
          )
          UPDATE messages SET next_check_time = lease
          WHERE id IN (SELECT id FROM target_messages)
          RETURNING *;
         */
        // CTE 1: Seleciona os N chats com mensagens mais antigas (sem lock ainda)
        var candidateChats = name("candidate_chats").as(
                select(MESSAGES.CHAT_ID)
                        .from(MESSAGES)
                        .where(MESSAGES.STATUS.equal(reservationPolicy.status().name()))
                        .and(MESSAGES.NEXT_CHECK_TIME.lessOrEqual(reservationPolicy.reservedAt()))
                        .groupBy(MESSAGES.CHAT_ID)
                        .orderBy(min(MESSAGES.NEXT_CHECK_TIME).asc()) // Ordena pela mensagem mais antiga do chat
                        .limit(reservationPolicy.maxChats() * 2)
        );

        // CTE 2: Identifica o ID da mensagem "líder" de cada chat candidato
        // Usa ROW_NUMBER para pegar a mensagem com menor next_check_time de cada chat
        var messageLeaders = name("message_leaders").as(
                select(
                        MESSAGES.CHAT_ID,
                        MESSAGES.ID.as("leader_id"),
                        rowNumber().over()
                                .partitionBy(MESSAGES.CHAT_ID)
                                .orderBy(MESSAGES.NEXT_CHECK_TIME.asc())
                                .as("rn")
                )
                        .from(MESSAGES)
                        .join(candidateChats).on(MESSAGES.CHAT_ID.equal(candidateChats.field(MESSAGES.CHAT_ID)))
                        .where(MESSAGES.STATUS.equal(reservationPolicy.status().name()))
        );

        // CTE 3: Aplica o LOCK apenas nas N mensagens líderes (1 por chat)
        var lockedLeaders = name("locked_leaders").as(
                select(
                        field(name("message_leaders", "leader_id"), UUID.class).as("id"),
                        field(name("message_leaders", "chat_id"), String.class).as("chat_id")
                )
                        .from(messageLeaders)
                        .where(field(name("message_leaders", "rn"), Integer.class).equal(1))
                        .orderBy(field(name("message_leaders", "chat_id")))
                        .limit(reservationPolicy.maxChats() * 2)
                        .forUpdate()
                        .skipLocked()
        );

        // CTE 4: Busca o lote de 500 mensagens dos chats que conseguimos o lock
        var targetMessages = name("target_messages").as(
                select(MESSAGES.ID)
                        .from(MESSAGES)
                        .where(MESSAGES.CHAT_ID.in(select(field(name("locked_leaders", "chat_id"), UUID.class)).from(lockedLeaders)))
                        .and(MESSAGES.STATUS.equal(reservationPolicy.status().name()))
                        .orderBy(MESSAGES.CHAT_ID, MESSAGES.NEXT_CHECK_TIME.asc())
                        .limit(reservationPolicy.maxMessages())
        );

        // UPDATE Final
        return this.writeOnlyDSLContext.with(candidateChats)
                .with(messageLeaders)
                .with(lockedLeaders)
                .with(targetMessages)
                .update(MESSAGES)
                .set(MESSAGES.NEXT_CHECK_TIME, reservationPolicy.reservedUntil())
                .where(MESSAGES.ID.in(select(field(name("target_messages", "id"), UUID.class)).from(targetMessages)))
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
                .orderBy(Objects.requireNonNull(MESSAGES.field(query.sort().name())).asc())
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
