package br.com.iolab.socia.infrastructure.chat.message.strategy.gemini.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.genai.types.Schema;
import com.google.genai.types.Type;

import java.util.Map;

public record Output(
        @JsonProperty(value = "message") String message,
        @JsonProperty(value = "escalate") Escalate escalate
) {
    public static final Schema SCHEMA;

    static {
        var messageSchema = Schema.builder()
                .description("A mensagem final para o usuário. Deve ser clara, direta e sem rascunhos ou pensamentos internos.")
                .type(Type.Known.STRING)
                .nullable(false)
                .build();

        var reasonSchema = Schema.builder()
                .description("O motivo da escalada. Este campo é obrigatório se o 'escalate' for usado.")
                .type(Type.Known.STRING)
                .nullable(true)
                .build();

        var escalateSchema = Schema.builder()
                .description("Objeto para escalar a conversa para um supervisor. Este objeto DEVE ser usado se a mensagem indicar a necessidade de intervenção humana (ex: 'acionei meu supervisor', 'houve um erro', etc.).")
                .type(Type.Known.OBJECT)
                .nullable(true)
                .properties(Map.of(
                        "reason", reasonSchema
                ))
                .required("reason")
                .build();

        SCHEMA = Schema.builder()
                .title("Output")
                .type(Type.Known.OBJECT)
                .properties(Map.of(
                        "message", messageSchema,
                        "escalate", escalateSchema
                ))
                .required("message")
                .build();
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