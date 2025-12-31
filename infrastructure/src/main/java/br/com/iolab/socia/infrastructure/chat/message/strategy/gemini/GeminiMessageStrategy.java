package br.com.iolab.socia.infrastructure.chat.message.strategy.gemini;

import br.com.iolab.commons.domain.exceptions.InternalErrorException;
import br.com.iolab.commons.json.Json;
import br.com.iolab.socia.domain.assistant.Assistant;
import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.MessageStrategy;
import br.com.iolab.socia.domain.chat.message.resource.MessageResource;
import br.com.iolab.socia.domain.chat.message.types.MessageContent;
import br.com.iolab.socia.domain.chat.message.types.MessageRoleType;
import br.com.iolab.socia.domain.chat.message.types.MessageStatusType;
import br.com.iolab.socia.infrastructure.chat.message.strategy.gemini.handler.GeminiHandler;
import br.com.iolab.socia.infrastructure.chat.message.strategy.gemini.schema.Output;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.FunctionCallingConfig;
import com.google.genai.types.FunctionCallingConfigMode;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.Part;
import com.google.genai.types.SafetySetting;
import com.google.genai.types.ToolConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static br.com.iolab.commons.types.Optionals.mapNullable;
import static br.com.iolab.socia.infrastructure.chat.message.strategy.gemini.schema.Output.SCHEMA;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiMessageStrategy implements MessageStrategy {
    private static final Float DEFAULT_TEMPERATURE = 0.25F;
    private static final Integer DEFAULT_MAX_FUNCTION_CALLS = 15;

    private final Client client;
    private final List<SafetySetting> safetySettings;

    @Override
    public @NonNull Message perform (
            @NonNull final Assistant assistant,
            @NonNull final Message message,
            @NonNull final Iterable<MessageResource> resources
    ) {

        var config = GenerateContentConfig.builder()
                .temperature(DEFAULT_TEMPERATURE)
                .responseMimeType(APPLICATION_JSON_VALUE)
                .responseSchema(SCHEMA)
                .systemInstruction(Content.builder()
                        .parts(Part.fromText("Seja gentil e educado"))
                        .build()
                )
                .safetySettings(safetySettings)
                .tools(Collections.emptyList())
                .toolConfig(ToolConfig.builder()
                        .functionCallingConfig(FunctionCallingConfig.builder()
                                .mode(FunctionCallingConfigMode.Known.AUTO)
                                .build()
                        )
                        .build()
                ).build();

        var content = Content.builder()
                .parts(Part.fromText(mapNullable(message.getContent(), MessageContent::value)))
                .build();

        var history = getHistory();
        history.addLast(content);

        var response = this.client.models.generateContent(assistant.getVersion(), history, config);

        var turnCounter = new AtomicInteger(0);
        var functionCalls = GeminiHandler.getFunctionCalls(response);
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
            output = Json.parse(response.text(), Output.class);
        } catch (Exception ex) {
            log.warn("Raciocínio embaralhado, resposta original: {}", response.text());
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
