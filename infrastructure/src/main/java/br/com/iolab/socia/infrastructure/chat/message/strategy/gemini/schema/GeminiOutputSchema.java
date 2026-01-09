package br.com.iolab.socia.infrastructure.chat.message.strategy.gemini.schema;

import com.google.cloud.vertexai.api.Schema;
import com.google.cloud.vertexai.api.Type;

import java.util.List;
import java.util.Map;

public final class GeminiOutputSchema {
    private static final Schema SCHEMA;

    static {
        var modeSchema = Schema.newBuilder()
                .setDescription("Modo da resposta. Obrigatório no root.")
                .setType(Type.STRING)
                .setNullable(false)
                .addAllEnum(List.of("ANSWER", "CLARIFY", "PLAN", "LOOKUP", "TASK_INTENT", "ESCALATE"))
                .build();

        var messagesSchema = Schema.newBuilder()
                .setDescription("Mensagem final para o usuário. Direta, sem rascunhos.")
                .setType(Type.STRING)
                .setNullable(false)
                .build();

        var messageSchema = Schema.newBuilder()
                .setDescription("Mensagem(ns) a ser(em) retornada(s) para o usuário, só separe-as se realmente fizer sentido envios separados e se forem de assuntos distintos, caso contrário, agrupe-as.")
                .setType(Type.ARRAY)
                .setNullable(false)
                .setItems(messagesSchema)
                .build();

        var knowledgeOpSchema = Schema.newBuilder()
                .setDescription("Operação de memória. Não use para senhas/tokens/credenciais.")
                .setType(Type.OBJECT)
                .setNullable(false)
                .putAllProperties(Map.of(
                        "op", Schema.newBuilder()
                                .setDescription("UPSERT, DELETE ou CANDIDATE.")
                                .setType(Type.STRING)
                                .setNullable(false)
                                .addAllEnum(List.of("UPSERT", "DELETE", "CANDIDATE"))
                                .build(),
                        "key", Schema.newBuilder()
                                .setDescription("Chave estável e curta.")
                                .setType(Type.STRING)
                                .setNullable(false)
                                .build(),
                        "value", Schema.newBuilder()
                                .setDescription("Valor a salvar. Em DELETE pode ser string vazia.")
                                .setType(Type.STRING)
                                .setNullable(false)
                                .build(),
                        "sensitivity", Schema.newBuilder()
                                .setDescription("PUBLIC, PRIVATE, CONFIDENTIAL.")
                                .setType(Type.STRING)
                                .setNullable(false)
                                .addAllEnum(List.of("PUBLIC", "PRIVATE", "CONFIDENTIAL"))
                                .build(),
                        "confidence", Schema.newBuilder()
                                .setDescription("0.0 a 1.0. Use alto apenas quando for estável e explícito.")
                                .setType(Type.NUMBER)
                                .setNullable(false)
                                .build(),
                        "ttl_days", Schema.newBuilder()
                                .setDescription("Dias de validade. Use 0 para sem TTL.")
                                .setType(Type.INTEGER)
                                .setNullable(false)
                                .build(),
                        "rationale", Schema.newBuilder()
                                .setDescription("1 linha do porquê isso é memória. Evite texto longo.")
                                .setType(Type.STRING)
                                .setNullable(false)
                                .build()
                ))
                .addAllRequired(List.of("op", "key", "value", "sensitivity", "confidence", "ttl_days", "rationale"))
                .build();

        var knowledgeOpsSchema = Schema.newBuilder()
                .setDescription("Lista de operações de memória (pode ser vazia).")
                .setType(Type.ARRAY)
                .setNullable(false)
                .setItems(knowledgeOpSchema)
                .build();

        var taskContextSchema = Schema.newBuilder()
                .setType(Type.OBJECT)
                .setNullable(false)
                .putAllProperties(Map.of(
                        "why", Schema.newBuilder()
                                .setType(Type.STRING)
                                .setNullable(false)
                                .setDescription("Por que essa tarefa existe.")
                                .build(),
                        "definition_of_done", Schema.newBuilder()
                                .setType(Type.STRING)
                                .setNullable(false)
                                .setDescription("Critério objetivo de pronto.")
                                .build(),
                        "dependencies", Schema.newBuilder()
                                .setType(Type.ARRAY)
                                .setNullable(false)
                                .setDescription("Dependências (IDs/chaves/itens).")
                                .setItems(Schema.newBuilder().setType(Type.STRING).setNullable(false).build())
                                .build()
                ))
                .addAllRequired(List.of("why", "definition_of_done", "dependencies"))
                .build();

        var taskOpSchema = Schema.newBuilder()
                .setDescription("Operações de tarefas. Só deve ser usado quando mode = TASK_INTENT.")
                .setType(Type.OBJECT)
                .setNullable(false)
                .putAllProperties(Map.of(
                        "op", Schema.newBuilder()
                                .setDescription("UPSERT, UPDATE, COMPLETE, DELETE ou CANDIDATE.")
                                .setType(Type.STRING)
                                .setNullable(false)
                                .addAllEnum(List.of("UPSERT", "UPDATE", "COMPLETE", "DELETE", "CANDIDATE"))
                                .build(),
                        "task_key", Schema.newBuilder()
                                .setDescription("Chave estável para idempotência. Ex: home.infiltracao.terraco.comprar_materiais")
                                .setType(Type.STRING)
                                .setNullable(false)
                                .build(),
                        "task_id", Schema.newBuilder()
                                .setDescription("ID interno (se existir). Se não houver, string vazia.")
                                .setType(Type.STRING)
                                .setNullable(false)
                                .build(),
                        "title", Schema.newBuilder()
                                .setDescription("Título executável: verbo + resultado.")
                                .setType(Type.STRING)
                                .setNullable(false)
                                .build(),
                        "owner", Schema.newBuilder()
                                .setDescription("socia|user|third_party")
                                .setType(Type.STRING)
                                .setNullable(false)
                                .addAllEnum(List.of("socia", "user", "third_party"))
                                .build(),
                        "due", Schema.newBuilder()
                                .setDescription("RFC3339 (ex: 2026-01-03T14:00:00-03:00) ou null.")
                                .setType(Type.STRING)
                                .setNullable(true)
                                .build(),
                        "priority", Schema.newBuilder()
                                .setDescription("P0|P1|P2|P3")
                                .setType(Type.STRING)
                                .setNullable(false)
                                .addAllEnum(List.of("P0", "P1", "P2", "P3"))
                                .build(),
                        "context", taskContextSchema,
                        "confidence", Schema.newBuilder()
                                .setDescription("0.0 a 1.0. Use alto apenas quando o usuário pediu explicitamente.")
                                .setType(Type.NUMBER)
                                .setNullable(false)
                                .build()
                ))
                .addAllRequired(List.of(
                        "op", "task_key", "task_id", "title", "owner", "due", "priority", "context", "confidence"
                ))
                .build();

        var taskOpsSchema = Schema.newBuilder()
                .setDescription("Lista de operações de tarefa (pode ser vazia). Deve ser vazia se mode != TASK_INTENT.")
                .setType(Type.ARRAY)
                .setNullable(false)
                .setItems(taskOpSchema)
                .build();

        var escalateSchema = Schema.newBuilder()
                .setDescription("Escalada para humano quando necessário.")
                .setType(Type.OBJECT)
                .setNullable(true)
                .putAllProperties(Map.of(
                        "reason", Schema.newBuilder()
                                .setDescription("Motivo curto.")
                                .setType(Type.STRING)
                                .setNullable(false)
                                .build(),
                        "message", Schema.newBuilder()
                                .setDescription("Mensagem curta para encaminhar.")
                                .setType(Type.STRING)
                                .setNullable(false)
                                .build()
                ))
                .addAllRequired(List.of("reason", "message"))
                .build();

        SCHEMA = Schema.newBuilder()
                .setTitle("Output")
                .setType(Type.OBJECT)
                .putAllProperties(Map.of(
                        "mode", modeSchema,
                        "messages", messageSchema,
                        "knowledge_ops", knowledgeOpsSchema,
                        "task_ops", taskOpsSchema,
                        "escalate", escalateSchema
                ))
                .addAllRequired(List.of("mode", "messages", "knowledge_ops", "task_ops"))
                .build();
    }

    private GeminiOutputSchema () {
    }

    public static Schema schema() {
        return SCHEMA;
    }
}
