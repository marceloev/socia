package br.com.iolab.socia.domain.assistant.knowledge;

import br.com.iolab.commons.domain.model.Model;
import br.com.iolab.commons.domain.validation.Result;
import br.com.iolab.socia.domain.assistant.AssistantID;
import br.com.iolab.socia.domain.assistant.knowledge.fields.KnowledgeKey;
import br.com.iolab.socia.domain.assistant.knowledge.fields.KnowledgeRationale;
import br.com.iolab.socia.domain.assistant.knowledge.fields.KnowledgeValue;
import br.com.iolab.socia.domain.assistant.knowledge.types.KnowledgeSensitivity;
import br.com.iolab.socia.domain.chat.ChatID;
import br.com.iolab.socia.domain.organization.OrganizationID;
import br.com.iolab.socia.domain.user.UserID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.time.Instant;

import static br.com.iolab.commons.domain.utils.InstantUtils.now;
import static br.com.iolab.commons.domain.utils.InstantUtils.plusDays;
import static br.com.iolab.commons.types.Checks.checkNonNull;
import static br.com.iolab.commons.types.Checks.checkPositive;

@Getter
@ToString
public class Knowledge extends Model<KnowledgeID> {
    private final AssistantID assistantID;
    private final OrganizationID organizationID;
    private final UserID userID;
    private final ChatID chatID;
    private final KnowledgeKey key;
    private final KnowledgeValue value;
    private final KnowledgeSensitivity knowledgeSensitivity;
    private final Double confidence;
    private final Integer ttlDays;
    private final KnowledgeRationale rationale;
    private final Instant expiresAt;

    @Builder(toBuilder = true, access = AccessLevel.PRIVATE)
    private Knowledge(
            @NonNull final KnowledgeID id,
            @NonNull final Instant createdAt,
            @NonNull final Instant updatedAt,
            final AssistantID assistantID,
            final OrganizationID organizationID,
            final UserID userID,
            final ChatID chatID,
            final KnowledgeKey key,
            final KnowledgeValue value,
            final KnowledgeSensitivity knowledgeSensitivity,
            final Double confidence,
            final Integer ttlDays,
            final KnowledgeRationale rationale,
            final Instant expiresAt
    ) {
        super(id, createdAt, updatedAt);
        this.assistantID = checkNonNull(assistantID, "AssistantID cannot be null!");
        this.organizationID = checkNonNull(organizationID, "OrganizationID cannot be null!");
        this.userID = checkNonNull(userID, "UserID cannot be null!");
        this.chatID = checkNonNull(chatID, "ChatID cannot be null!");
        this.key = checkNonNull(key, "Key cannot be null!");
        this.value = checkNonNull(value, "Value cannot be null!");
        this.knowledgeSensitivity = checkNonNull(knowledgeSensitivity, "Sensitivity cannot be null!");
        this.confidence = checkPositive(confidence, "Confidence must be a positive number!");
        this.ttlDays = ttlDays;
        this.rationale = checkNonNull(rationale, "Rationale cannot be null!");
        this.expiresAt = expiresAt;
    }

    public static Result<Knowledge> create(
            final AssistantID assistantID,
            final OrganizationID organizationID,
            final UserID userID,
            final ChatID chatID,
            final KnowledgeKey key,
            final KnowledgeValue value,
            final KnowledgeSensitivity knowledgeSensitivity,
            final Double confidence,
            final Integer ttlDays,
            final KnowledgeRationale rationale
    ) {
        var now = now();
        var expiresAt = ttlDays != null ? plusDays(now, ttlDays) : null;
        
        return new Knowledge(
                KnowledgeID.generate(now),
                now,
                now,
                assistantID,
                organizationID,
                userID,
                chatID,
                key,
                value,
                knowledgeSensitivity,
                confidence,
                ttlDays,
                rationale,
                expiresAt
        ).validate();
    }

    public static @NonNull Knowledge with(
            final KnowledgeID id,
            final Instant createdAt,
            final Instant updatedAt,
            final AssistantID assistantID,
            final OrganizationID organizationID,
            final UserID userID,
            final ChatID chatID,
            final KnowledgeKey key,
            final KnowledgeValue value,
            final KnowledgeSensitivity knowledgeSensitivity,
            final Double confidence,
            final Integer ttlDays,
            final KnowledgeRationale rationale,
            final Instant expiresAt
    ) {
        return new Knowledge(
                id,
                createdAt,
                updatedAt,
                assistantID,
                organizationID,
                userID,
                chatID,
                key,
                value,
                knowledgeSensitivity,
                confidence,
                ttlDays,
                rationale,
                expiresAt
        );
    }

    @Override
    protected Result<Knowledge> validate() {
        var result = Result.builder(this);
        
        if (confidence < 0.0 || confidence > 1.0) {
            result.appendError("Confidence must be between 0.0 and 1.0");
        }
        
        if (ttlDays != null && ttlDays <= 0) {
            result.appendError("TTL must be greater than zero");
        }
        
        return result.build();
    }

    public Result<Knowledge> update(
            final KnowledgeValue value,
            final Double confidence,
            final KnowledgeRationale rationale
    ) {
        return this.toBuilder()
                .value(value)
                .confidence(confidence)
                .rationale(rationale)
                .updatedAt(now())
                .build()
                .validate();
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(now());
    }
}
