package br.com.iolab.socia.infrastructure.chat.message.strategy.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TaskContext(
        @JsonProperty("why") String why,
        @JsonProperty("definition_of_done") String definitionOfDone,
        @JsonProperty("dependencies") List<String> dependencies
) {}
