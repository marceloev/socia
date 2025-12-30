package br.com.iolab.socia.domain.chat.message;

import br.com.iolab.socia.domain.assistant.Assistant;
import br.com.iolab.socia.domain.chat.message.resource.MessageResource;
import lombok.NonNull;

public interface MessageStrategy {
    @NonNull
    Message perform (
            @NonNull final Assistant assistant,
            @NonNull final Message message,
            @NonNull final Iterable<MessageResource> resources
    );
}
