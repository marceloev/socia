package br.com.iolab.socia.domain.assistant.instance;

import br.com.iolab.socia.domain.chat.Chat;
import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.resource.MessageResource;
import lombok.NonNull;

import java.util.Collection;

public interface InstanceStrategy {
    void send (
            @NonNull Instance instance,
            @NonNull Chat chat,
            @NonNull Collection<Message> message
    );

    MessageResource retrieveFile (
            @NonNull Instance instance,
            @NonNull String fileId
    );
}
