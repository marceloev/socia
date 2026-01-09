package br.com.iolab.socia.infrastructure.assistant.knowledge.persistence;

import br.com.iolab.commons.infrastructure.persistence.BasicModelGateway;
import br.com.iolab.infrastructure.jooq.generated.tables.records.KnowledgeRecord;
import br.com.iolab.socia.domain.assistant.AssistantID;
import br.com.iolab.socia.domain.assistant.knowledge.Knowledge;
import br.com.iolab.socia.domain.assistant.knowledge.KnowledgeGateway;
import br.com.iolab.socia.domain.assistant.knowledge.KnowledgeID;
import br.com.iolab.socia.domain.assistant.knowledge.fields.KnowledgeKey;
import lombok.NonNull;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static br.com.iolab.infrastructure.jooq.generated.tables.Knowledge.KNOWLEDGE;

@Repository
public class KnowledgeGatewayImpl extends BasicModelGateway<Knowledge, KnowledgeID, KnowledgeRecord> implements KnowledgeGateway {
    protected KnowledgeGatewayImpl (
            final DSLContext readOnlyDSLContext,
            final DSLContext writeOnlyDSLContext,
            final KnowledgeMapperImpl knowledgeMapper
    ) {
        super(
                readOnlyDSLContext,
                writeOnlyDSLContext,
                knowledgeMapper,
                KNOWLEDGE,
                KNOWLEDGE.ID,
                KNOWLEDGE.UPDATED_AT
        );
    }

    @Override
    public @NonNull List<Knowledge> findByAssistant (@NonNull final AssistantID assistantID) {
        return this.readOnlyDSLContext
                .selectFrom(KNOWLEDGE)
                .where(KNOWLEDGE.ASSISTANT_ID.eq(assistantID.value()))
                .and(KNOWLEDGE.EXPIRES_AT.isNull().or(KNOWLEDGE.EXPIRES_AT.greaterThan(Instant.now())))
                .orderBy(KNOWLEDGE.CREATED_AT.desc())
                .fetch()
                .map(this.mapper::toModel);
    }

    @Override
    public @NonNull Optional<Knowledge> findByKey (@NonNull final AssistantID assistantID, @NonNull final KnowledgeKey key) {
        return this.readOnlyDSLContext
                .selectFrom(KNOWLEDGE)
                .where(KNOWLEDGE.ASSISTANT_ID.eq(assistantID.value()))
                .and(KNOWLEDGE.KEY.eq(key.value()))
                .and(KNOWLEDGE.EXPIRES_AT.isNull().or(KNOWLEDGE.EXPIRES_AT.greaterThan(Instant.now())))
                .fetchOptional()
                .map(this.mapper::toModel);
    }

    @Override
    public void deleteExpired () {
        this.writeOnlyDSLContext
                .deleteFrom(KNOWLEDGE)
                .where(KNOWLEDGE.EXPIRES_AT.isNotNull())
                .and(KNOWLEDGE.EXPIRES_AT.lessOrEqual(Instant.now()))
                .execute();
    }

    @Override
    public void upsert (@NonNull final Knowledge knowledge) {
        final var record = this.mapper.fromModel(knowledge);

        this.writeOnlyDSLContext
                .insertInto(KNOWLEDGE)
                .set(record)
                .onConflict(KNOWLEDGE.ASSISTANT_ID, KNOWLEDGE.KEY)
                .doUpdate()
                .set(KNOWLEDGE.UPDATED_AT, record.getUpdatedAt())
                .set(KNOWLEDGE.VALUE, record.getValue())
                .set(KNOWLEDGE.KNOWLEDGESENSITIVITY, record.getKnowledgesensitivity())
                .set(KNOWLEDGE.CONFIDENCE, record.getConfidence())
                .set(KNOWLEDGE.TTL_DAYS, record.getTtlDays())
                .set(KNOWLEDGE.RATIONALE, record.getRationale())
                .set(KNOWLEDGE.EXPIRES_AT, record.getExpiresAt())
                .execute();
    }
}
