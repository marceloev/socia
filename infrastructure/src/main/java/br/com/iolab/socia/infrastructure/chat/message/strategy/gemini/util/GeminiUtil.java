package br.com.iolab.socia.infrastructure.chat.message.strategy.gemini.util;

import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.types.MessageRoleType;
import lombok.NonNull;

public class GeminiUtil {
    public static @NonNull String role (@NonNull final Message message) {
        return MessageRoleType.USER.equals(message.getRole()) ? "user" : "model";
    }
}
