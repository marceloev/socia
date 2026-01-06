package br.com.iolab.socia.infrastructure.chat.message.strategy.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KnowledgeOp(
        @JsonProperty("op") KnowledgeOperation op,
        @JsonProperty("key") String key,
        @JsonProperty("value") String value,
        @JsonProperty("sensitivity") Sensitivity sensitivity,
        @JsonProperty("confidence") Double confidence,
        @JsonProperty("ttl_days") Integer ttlDays,
        @JsonProperty("rationale") String rationale
) {}
