package br.com.iolab.socia.infrastructure.chat.message.strategy.gemini;

import br.com.iolab.commons.domain.exceptions.InternalErrorException;
import br.com.iolab.commons.json.Json;
import br.com.iolab.socia.domain.assistant.Assistant;
import br.com.iolab.socia.domain.chat.Chat;
import br.com.iolab.socia.domain.chat.ChatID;
import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.MessageGateway;
import br.com.iolab.socia.domain.chat.message.MessageStrategy;
import br.com.iolab.socia.domain.chat.message.types.MessageContent;
import br.com.iolab.socia.infrastructure.assistant.persistence.AssistantPromptProvider;
import br.com.iolab.socia.infrastructure.chat.message.strategy.gemini.schema.Output;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.FunctionCallingConfig;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.Part;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.api.ToolConfig;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.protobuf.util.JsonFormat;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static br.com.iolab.socia.domain.chat.message.types.MessageRoleType.ASSISTANT;
import static br.com.iolab.socia.domain.chat.message.types.MessageStatusType.COMPLETED;
import static br.com.iolab.socia.infrastructure.chat.message.strategy.gemini.schema.Output.schema;
import static br.com.iolab.socia.infrastructure.chat.message.strategy.gemini.util.GeminiUtil.role;
import static com.google.cloud.vertexai.api.FunctionCallingConfig.Mode.AUTO;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiMessageStrategy implements MessageStrategy {
    private static final Float DEFAULT_TEMPERATURE = 0.25F;
    private static final Integer DEFAULT_MAX_FUNCTION_CALLS = 15;
    private static final JsonFormat.Printer DEFAULT_JSON_PRINTER = JsonFormat.printer();
    private static final JsonFormat.Parser DEFAULT_JSON_PARSER = JsonFormat.parser();

    private final MessageGateway messageGateway;
    private final AssistantPromptProvider assistantPromptProvider;

    private final VertexAI vertexAI;
    private final List<SafetySetting> safetySettings;

    @Override
    public @NonNull Message perform (
            @NonNull final Assistant assistant,
            @NonNull final Chat chat
    ) {
        try {
            var model = new GenerativeModel.Builder()
                    .setVertexAi(vertexAI)
                    .setModelName(assistant.getVersion())
                    .setSafetySettings(safetySettings)
                    .setSystemInstruction(Content.newBuilder()
                                    .addAllParts(getAllPrompts(assistant))
                                    .build()
                /*).setTools(Collections.singletonList(Tool.newBuilder()
                        .addAllFunctionDeclarations(Collections.emptyList())
                        .build()
                )*/).setToolConfig(ToolConfig.newBuilder()
                            .setFunctionCallingConfig(FunctionCallingConfig.newBuilder()
                                    .setMode(AUTO)
                                    .build())
                            .build()
                    ).setGenerationConfig(GenerationConfig.newBuilder()
                            .setTemperature(DEFAULT_TEMPERATURE)
                            .setResponseMimeType(APPLICATION_JSON_VALUE)
                            .setResponseSchema(schema())
                            .build()
                    ).build();

            var history = findChatHistory(chat.getId());
            var response = model.generateContent(history);

            var turnCounter = new AtomicInteger(0);
            var functionCalls = ResponseHandler.getFunctionCalls(response);
            while (!functionCalls.isEmpty()) {
                if (turnCounter.incrementAndGet() > DEFAULT_MAX_FUNCTION_CALLS) {
                    log.warn("Mais de {} turnos de function calls, cancelando operação!", DEFAULT_MAX_FUNCTION_CALLS);
                    break;
                }

                for (var functionCall : functionCalls) {

                }

                //functionCalls = ResponseHandler.getFunctionCalls(chatResponse);

                throw InternalErrorException.with("Não implementado!");
            }

            Output output;
            try {
                var responseContent = ResponseHandler.getText(response);
                output = Json.parse(responseContent, Output.class);
                log.debug("Gemini response: {}", responseContent);
            } catch (Exception ex) {
                log.warn("Raciocínio embaralhado, resposta original: {}", ResponseHandler.getText(response));
                output = Output.empty();
            }

            return Message.create(
                    chat.getId(),
                    COMPLETED,
                    ASSISTANT,
                    MessageContent.of(output.message()),
                    Collections.emptyMap()
            ).successOrThrow();
        } catch (Exception ex) {
            log.error("Error while trying to process chat: {}", chat.getId(), ex);

            return Message.create(
                    chat.getId(),
                    COMPLETED,
                    ASSISTANT,
                    MessageContent.of("Não foi possível concluir a requisição, por favor, contate o suporte!"),
                    Collections.emptyMap()
            ).successOrThrow();
        }
    }

    private @NonNull List<Content> findChatHistory (@NonNull final ChatID chatID) {
        return this.messageGateway.findAllByChatID(chatID).stream()
                .flatMap(message -> {
                    var contents = new ArrayList<Content>();

                    if (!message.getMetadata().isEmpty()) {
                        int toolTurnCounter = 0;
                        while (message.getMetadata().containsKey("function_ask_" + toolTurnCounter)) {
                            try {
                                // Adiciona o FunctionCall que o modelo fez
                                String callJson = message.getMetadata().get("function_ask_" + toolTurnCounter);
                                Content.Builder callBuilder = Content.newBuilder();
                                DEFAULT_JSON_PARSER.merge(callJson, callBuilder);
                                contents.add(callBuilder.build());

                                // Adiciona o FunctionResponse que nosso código forneceu
                                if (message.getMetadata().containsKey("function_answer_" + toolTurnCounter)) {
                                    String responseJson = message.getMetadata().get("function_answer_" + toolTurnCounter);
                                    Content.Builder responseBuilder = Content.newBuilder();
                                    DEFAULT_JSON_PARSER.merge(responseJson, responseBuilder);
                                    contents.add(responseBuilder.build());
                                }
                            } catch (Exception e) {
                                log.error("Erro ao reconstruir o histórico de ferramentas do metadata.", e);
                                throw InternalErrorException.with("Não foi possível recuperar as ferramentas do histórico de conversas", e);
                            }

                            toolTurnCounter++;
                        }
                    }

                    var parts = new ArrayList<Part>();
                    parts.add(Part.newBuilder()
                            .setText(message.getContent().value())
                            .build()
                    );

                    contents.add(Content.newBuilder()
                            .setRole(role(message))
                            .addAllParts(parts)
                            .build());

                    return contents.stream();
                }).toList().reversed();
    }

    private List<Part> getAllPrompts (@NonNull final Assistant assistant) {
        var parts = new ArrayList<Part>();

        parts.add(Part.newBuilder()
                .setText(assistant.getPrompt())
                .build()
        );

        var prompts = this.assistantPromptProvider.getPrompt();
        parts.add(Part.newBuilder()
                .setText(prompts.core())
                .build()
        );

        return parts;
    }
}
