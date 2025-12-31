package br.com.iolab.socia.infrastructure.chat.message.strategy.gemini.handler;

import br.com.iolab.commons.types.Streams;
import com.google.common.collect.ImmutableList;
import com.google.genai.types.FunctionCall;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import lombok.NonNull;

import java.util.stream.Collectors;

public class GeminiHandler {
    public static ImmutableList<FunctionCall> getFunctionCalls (@NonNull final GenerateContentResponse response) {
        ImmutableList<Part> parts = response.parts();
        if (parts == null || parts.isEmpty()) {
            return ImmutableList.of();
        }

        return ImmutableList.copyOf(Streams.streamOf(parts)
                .filter(part -> part.functionCall().isPresent())
                .map(part -> part.functionCall().get())
                .collect(Collectors.toList())
        );
    }
}
