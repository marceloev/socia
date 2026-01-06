package br.com.iolab.socia.infrastructure.chat.message.strategy.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Escalate(
        @JsonProperty("reason") String reason,
        @JsonProperty("message") String message
) {}
