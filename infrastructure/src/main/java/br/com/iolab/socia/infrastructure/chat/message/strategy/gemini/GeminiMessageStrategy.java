package br.com.iolab.socia.infrastructure.chat.message.strategy.gemini;

import br.com.iolab.commons.domain.exceptions.InternalErrorException;
import br.com.iolab.commons.domain.validation.Result;
import br.com.iolab.commons.json.Json;
import br.com.iolab.commons.types.Streams;
import br.com.iolab.socia.domain.assistant.knowledge.Knowledge;
import br.com.iolab.socia.domain.assistant.knowledge.fields.KnowledgeKey;
import br.com.iolab.socia.domain.assistant.knowledge.fields.KnowledgeRationale;
import br.com.iolab.socia.domain.assistant.knowledge.fields.KnowledgeValue;
import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.strategy.MessageStrategy;
import br.com.iolab.socia.domain.chat.message.strategy.perform.PerformMessageStrategyInput;
import br.com.iolab.socia.domain.chat.message.strategy.perform.PerformMessageStrategyOutput;
import br.com.iolab.socia.domain.chat.message.types.MessageContent;
import br.com.iolab.socia.infrastructure.assistant.persistence.AssistantPromptProvider;
import br.com.iolab.socia.infrastructure.chat.message.strategy.schema.Output;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Blob;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.FunctionCallingConfig;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.Part;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.api.ToolConfig;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import com.google.protobuf.ByteString;
import com.google.protobuf.util.JsonFormat;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static br.com.iolab.socia.domain.chat.message.types.MessageRoleType.ASSISTANT;
import static br.com.iolab.socia.domain.chat.message.types.MessageStatusType.COMPLETED;
import static br.com.iolab.socia.infrastructure.chat.message.strategy.gemini.schema.GeminiOutputSchema.schema;
import static br.com.iolab.socia.infrastructure.chat.message.strategy.gemini.util.GeminiUtil.mimeType;
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

    private final VertexAI vertexAI;
    private final List<SafetySetting> safetySettings;
    private final AssistantPromptProvider assistantPromptProvider;

    @Override
    public @NonNull PerformMessageStrategyOutput perform (@NonNull final PerformMessageStrategyInput input) {
        try {
            var model = new GenerativeModel.Builder()
                    .setVertexAi(vertexAI)
                    .setModelName(input.getAssistant().getVersion())
                    .setSafetySettings(safetySettings)
                    .setSystemInstruction(Content.newBuilder()
                                    .addAllParts(getAllPrompts(input))
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

            var historyContent = buildHistoryContent(input);
            var response = model.generateContent(historyContent);

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

            return PerformMessageStrategyOutput.builder()
                    .message(Message.create(
                            input.getChat().getId(),
                            COMPLETED,
                            ASSISTANT,
                            MessageContent.of(output.message()),
                            Collections.emptyMap()
                    ).successOrThrow())
                    .resources(Collections.emptyList())
                    .knowledge(Streams.streamOf(output.knowledgeOps())
                            .map(knowledgeOp -> Knowledge.create(
                                    input.getAssistant().getId(),
                                    input.getAssistant().getOrganizationID(),
                                    input.getChat().getUserID(),
                                    input.getChat().getId(),
                                    KnowledgeKey.of(knowledgeOp.key()),
                                    KnowledgeValue.of(knowledgeOp.value()),
                                    knowledgeOp.sensitivity(),
                                    knowledgeOp.confidence(),
                                    (knowledgeOp.ttlDays() == 0 ? null : knowledgeOp.ttlDays()),
                                    KnowledgeRationale.of(knowledgeOp.rationale())
                            ))
                            .map(Result::successOrThrow)
                            .toList()
                    ).build();
        } catch (Exception ex) {
            log.error("Error while trying to process chat: {}", input.getChat().getId(), ex);

            return PerformMessageStrategyOutput.builder()
                    .message(Message.create(
                            input.getChat().getId(),
                            COMPLETED,
                            ASSISTANT,
                            MessageContent.of("Não foi possível concluir a requisição, por favor, contate o suporte!"),
                            Collections.emptyMap()
                    ).successOrThrow())
                    .resources(Collections.emptyList())
                    .knowledge(Collections.emptyList())
                    .build();
        }
    }

    private @NonNull List<Content> buildHistoryContent (@NonNull final PerformMessageStrategyInput input) {
        return input.getHistory().stream()
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

                    if (message.getContent() != null) {
                        parts.add(Part.newBuilder()
                                .setText(message.getContent().value())
                                .build()
                        );
                    }

                    Optional.ofNullable(input.getResources().get(message.getId()))
                            .ifPresent(resources -> {
                                resources.forEach(resource ->
                                        parts.add(Part.newBuilder()
                                                .setInlineData(Blob.newBuilder()
                                                        .setMimeType(mimeType(resource))
                                                        .setData(ByteString.copyFrom(resource.getFile()))
                                                        .build()
                                                ).build())
                                );
                            });

                    contents.add(Content.newBuilder()
                            .setRole(role(message))
                            .addAllParts(parts)
                            .build());

                    return contents.stream();
                }).toList().reversed();
    }

    private List<Part> getAllPrompts (@NonNull final PerformMessageStrategyInput input) {
        var parts = new ArrayList<Part>();

        parts.add(Part.newBuilder()
                .setText(input.getAssistant().getPrompt())
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
