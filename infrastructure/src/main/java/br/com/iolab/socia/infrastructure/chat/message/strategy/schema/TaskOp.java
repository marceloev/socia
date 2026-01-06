package br.com.iolab.socia.infrastructure.chat.message.strategy.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TaskOp(
        @JsonProperty("op") TaskOperation op,
        @JsonProperty("task_key") String taskKey,
        @JsonProperty("task_id") String taskId,
        @JsonProperty("title") String title,
        @JsonProperty("owner") Owner owner,
        @JsonProperty("due") String due,
        @JsonProperty("priority") Priority priority,
        @JsonProperty("context") TaskContext context,
        @JsonProperty("confidence") Double confidence
) {}
