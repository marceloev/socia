package br.com.iolab.socia.infrastructure.chat.message.strategy.gemini.util;

import br.com.iolab.commons.domain.exceptions.BadRequestException;
import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.resource.MessageResource;
import br.com.iolab.socia.domain.chat.message.types.MessageRoleType;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GeminiUtil {
    private static final Map<String, String> SUPPORTED_MIME_TYPES = new HashMap<>();

    static {
        SUPPORTED_MIME_TYPES.put("audio/ogg", "audio/mp3");
        SUPPORTED_MIME_TYPES.put("image/png", "image/png");
        SUPPORTED_MIME_TYPES.put("image/webp", "image/png");
        SUPPORTED_MIME_TYPES.put("image/jpeg", "image/jpeg");
        SUPPORTED_MIME_TYPES.put("application/pdf", "application/pdf");
        SUPPORTED_MIME_TYPES.put("video/mp4", "video/mp4");
        SUPPORTED_MIME_TYPES.put("text/plain", "text/plain");
        SUPPORTED_MIME_TYPES.put("text/html", "text/plain");
        SUPPORTED_MIME_TYPES.put("text/xml", "text/plain");
        SUPPORTED_MIME_TYPES.put("text/css", "text/plain");
        SUPPORTED_MIME_TYPES.put("text/csv", "text/csv");
        SUPPORTED_MIME_TYPES.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "text/csv");
    }

    public static String mimeType (@NonNull final MessageResource messageResource) {
        return Optional.ofNullable(SUPPORTED_MIME_TYPES.get(messageResource.getContentType()))
                .orElseThrow(() -> BadRequestException.with("Tipo de arquivo no suportado: " + messageResource.getContentType()));
    }

    public static @NonNull String role (@NonNull final Message message) {
        return MessageRoleType.USER.equals(message.getRole()) ? "user" : "model";
    }
}
