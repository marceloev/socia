package br.com.iolab.socia.infrastructure.assistant.knowledge.persistence;

import br.com.iolab.commons.domain.model.ModelID;
import br.com.iolab.commons.domain.model.ModelMapper;
import br.com.iolab.infrastructure.jooq.generated.tables.records.KnowledgeRecord;
import br.com.iolab.socia.domain.assistant.AssistantID;
import br.com.iolab.socia.domain.assistant.knowledge.Knowledge;
import br.com.iolab.socia.domain.assistant.knowledge.KnowledgeID;
import br.com.iolab.socia.domain.assistant.knowledge.fields.KnowledgeKey;
import br.com.iolab.socia.domain.assistant.knowledge.fields.KnowledgeRationale;
import br.com.iolab.socia.domain.assistant.knowledge.fields.KnowledgeValue;
import br.com.iolab.socia.domain.assistant.knowledge.types.KnowledgeSensitivity;
import br.com.iolab.socia.domain.chat.ChatID;
import br.com.iolab.socia.domain.organization.OrganizationID;
import br.com.iolab.socia.domain.user.UserID;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static br.com.iolab.commons.types.Optionals.mapNullable;

@Service
public class KnowledgeMapperImpl extends ModelMapper<Knowledge, KnowledgeRecord> {
    @Override
    public @NonNull KnowledgeRecord fromModel (@NonNull final Knowledge knowledge) {
        return new KnowledgeRecord(
                mapNullable(knowledge.getId(), ModelID::value),
                knowledge.getCreatedAt(),
                knowledge.getUpdatedAt(),
                mapNullable(knowledge.getAssistantID(), ModelID::value),
                mapNullable(knowledge.getOrganizationID(), ModelID::value),
                mapNullable(knowledge.getUserID(), ModelID::value),
                mapNullable(knowledge.getChatID(), ModelID::value),
                mapNullable(knowledge.getKey(), KnowledgeKey::value),
                mapNullable(knowledge.getValue(), KnowledgeValue::value),
                mapNullable(knowledge.getKnowledgeSensitivity(), Enum::name),
                mapNullable(knowledge.getConfidence(), BigDecimal::valueOf),
                knowledge.getTtlDays(),
                mapNullable(knowledge.getRationale(), KnowledgeRationale::value),
                knowledge.getExpiresAt()
        );
    }

    @Override
    public @NonNull Knowledge toModel (@NonNull final KnowledgeRecord knowledgeRecord) {
        return Knowledge.with(
                mapNullable(knowledgeRecord.getId(), KnowledgeID::from),
                knowledgeRecord.getCreatedAt(),
                knowledgeRecord.getUpdatedAt(),
                mapNullable(knowledgeRecord.getAssistantId(), AssistantID::from),
                mapNullable(knowledgeRecord.getOrganizationId(), OrganizationID::from),
                mapNullable(knowledgeRecord.getUserId(), UserID::from),
                mapNullable(knowledgeRecord.getChatId(), ChatID::from),
                mapNullable(knowledgeRecord.getKey(), KnowledgeKey::of),
                mapNullable(knowledgeRecord.getValue(), KnowledgeValue::of),
                mapNullable(knowledgeRecord.getSensitivity(), KnowledgeSensitivity::valueOf),
                mapNullable(knowledgeRecord.getConfidence(), BigDecimal::doubleValue),
                knowledgeRecord.getTtlDays(),
                mapNullable(knowledgeRecord.getRationale(), KnowledgeRationale::of),
                knowledgeRecord.getExpiresAt()
        );
    }
}
