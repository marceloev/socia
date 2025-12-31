package br.com.iolab.socia.infrastructure.chat.message.strategy.gemini;

import br.com.iolab.commons.domain.exceptions.BadRequestException;
import br.com.iolab.commons.domain.exceptions.InternalErrorException;
import br.com.iolab.commons.json.Json;
import br.com.iolab.socia.domain.assistant.Assistant;
import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.MessageStrategy;
import br.com.iolab.socia.domain.chat.message.resource.MessageResource;
import br.com.iolab.socia.domain.chat.message.types.MessageContent;
import br.com.iolab.socia.domain.chat.message.types.MessageRoleType;
import br.com.iolab.socia.domain.chat.message.types.MessageStatusType;
import br.com.iolab.socia.infrastructure.chat.message.strategy.gemini.schema.Output;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.FunctionCallingConfig;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.Part;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.api.Tool;
import com.google.cloud.vertexai.api.ToolConfig;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.cloud.vertexai.api.FunctionCallingConfig.Mode.AUTO;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiMessageStrategy implements MessageStrategy {
    private static final Float DEFAULT_TEMPERATURE = 0.25F;
    private static final Integer DEFAULT_MAX_FUNCTION_CALLS = 15;

    private final VertexAI vertexAI;
    private final List<SafetySetting> safetySettings;

    @Override
    public @NonNull Message perform (
            @NonNull final Assistant assistant,
            @NonNull final Message message,
            @NonNull final Iterable<MessageResource> resources
    ) {
        var model = new GenerativeModel.Builder()
                .setVertexAi(vertexAI)
                .setModelName(assistant.getVersion())
                .setSafetySettings(safetySettings)
                .setSystemInstruction(Content.newBuilder()
                        .addParts(Part.newBuilder()
                                .setText("Seja gentil")
                        ).build()
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
                        .setResponseSchema(Output.schema())
                        .build()
                ).build();

        var chatSession = new ChatSession(model);
        //chatSession.setHistory();

        var chatRequest = Content.newBuilder()
                .setRole(MessageRoleType.USER.equals(message.getRole()) ? "user" : "model")
                .addParts(Part.newBuilder()
                        .setText(message.getContent().value())
                        .build())
                .build();

        GenerateContentResponse chatResponse;
        try {
            chatResponse = chatSession.sendMessage(chatRequest);
        } catch (IOException e) {
            throw BadRequestException.with("Não foi possível comunicar com Gemini!");
        }

        var turnCounter = new AtomicInteger(0);
        var functionCalls = ResponseHandler.getFunctionCalls(chatResponse);
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
            output = Json.parse(ResponseHandler.getText(chatResponse), Output.class);
        } catch (Exception ex) {
            log.warn("Raciocínio embaralhado, resposta original: {}", ResponseHandler.getText(chatResponse));
            output = Output.empty();
        }

        return Message.create(
                message.getChatID(),
                MessageStatusType.COMPLETED,
                MessageRoleType.ASSISTANT,
                MessageContent.of(output.message())
        ).successOrThrow();
    }

    private @NonNull List<Content> getHistory () {
        return new ArrayList<>();
    }
}
