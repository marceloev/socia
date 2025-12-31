package br.com.iolab.socia.infrastructure.chat.message.strategy.gemini.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.vertexai.api.Schema;
import com.google.cloud.vertexai.api.Type;

import java.util.Map;

public record Output(
        @JsonProperty(value = "message") String message,
        @JsonProperty(value = "escalate") Escalate escalate
) {
    private static final Schema SCHEMA;

    static {
        var messageSchema = Schema.newBuilder()
                .setDescription("A mensagem final para o usuário. Deve ser clara, direta e sem rascunhos ou pensamentos internos.")
                .setType(Type.STRING)
                .setNullable(false)
                .build();

        var reasonSchema = Schema.newBuilder()
                .setDescription("O motivo da escalada. Este campo é obrigatório se o 'escalate' for usado.")
                .setType(Type.STRING)
                .setNullable(true)
                .build();

        var escalateSchema = Schema.newBuilder()
                .setDescription("Objeto para escalar a conversa para um supervisor. Este objeto DEVE ser usado se a mensagem indicar a necessidade de intervenção humana (ex: 'acionei meu supervisor', 'houve um erro', etc.).")
                .setType(Type.OBJECT)
                .setNullable(true)
                .putAllProperties(Map.of(
                        "reason", reasonSchema
                ))
                .addRequired("reason")
                .build();

        SCHEMA = Schema.newBuilder()
                .setTitle("Output")
                .setType(Type.OBJECT)
                .putAllProperties(Map.of(
                        "message", messageSchema,
                        "escalate", escalateSchema
                ))
                .addRequired("message")
                .build();
    }

    public static Schema schema () {
        return SCHEMA;
    }

    public static Output empty () {
        return new Output(
                """
                        Peço desculpas! Parece que meu raciocínio se embaralhou por um instante.
                        
                        Já reiniciei meu foco para te atender melhor. Poderia, por favor, me dizer o que precisa novamente? Às vezes, formular o pedido com outras palavras ajuda.
                        Se preferir, posso escalar para meu supervisor, mas se puder tentar novamente, estou pronto para recomeçar!
                        """,
                null
        );
    }

    public record Escalate(
            @JsonProperty(value = "reason") String reason,
            @JsonProperty(value = "message") String message
    ) {
    }
}