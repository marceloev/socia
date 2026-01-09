package br.com.iolab.socia.infrastructure.chat.message.strategy.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public record Output(
        @JsonProperty("mode") Mode mode,
        @JsonProperty("messages") List<String> messages,
        @JsonProperty("knowledge_ops") List<KnowledgeOp> knowledgeOps,
        @JsonProperty("task_ops") List<TaskOp> taskOps,
        @JsonProperty("escalate") Escalate escalate
) {
    public static Output empty() {
        return new Output(
                Mode.ESCALATE,
                Collections.singletonList("Tive um problema para estruturar a resposta agora. Me diz de novo, em uma frase, o que você precisa."),
                List.of(),
                List.of(),
                new Escalate("OUTPUT_ERROR", "Falha ao gerar/validar JSON no schema esperado.")
        );
    }
}