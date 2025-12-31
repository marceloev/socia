package br.com.iolab.socia.domain.chat.message;

import br.com.iolab.socia.domain.assistant.Assistant;
import br.com.iolab.socia.domain.chat.Chat;
import lombok.NonNull;

public interface MessageStrategy {
    @NonNull
    Message perform (
            @NonNull final Assistant assistant,
            @NonNull final Chat chat
    );
}
