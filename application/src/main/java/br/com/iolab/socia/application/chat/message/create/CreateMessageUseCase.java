package br.com.iolab.socia.application.chat.message.create;

import br.com.iolab.commons.application.UseCase;
import br.com.iolab.commons.types.fields.Phone;
import br.com.iolab.socia.domain.assistant.instance.InstanceID;
import br.com.iolab.socia.domain.chat.ChatID;
import br.com.iolab.socia.domain.chat.message.MessageID;
import br.com.iolab.socia.domain.chat.message.resource.types.MessageResourceType;
import br.com.iolab.socia.domain.chat.message.types.MessageContent;
import lombok.NonNull;

import java.util.List;

public abstract class CreateMessageUseCase extends UseCase<CreateMessageUseCase.Input, CreateMessageUseCase.Output> {
    public abstract @NonNull Output perform (@NonNull Input input);

    public record Input(
            InstanceID instanceID,
            Phone from,
            Phone to,
            MessageContent content,
            List<MessageResource> resources
    ) {
        public record MessageResource(
                MessageResourceType type,
                String contentType,
                byte[] content
        ) {}
    }

    public record Output(
            MessageID messageID,
            ChatID chatID
    ) {
    }
}
