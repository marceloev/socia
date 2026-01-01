package br.com.iolab.socia.application.chat.message.create;

import br.com.iolab.commons.application.UseCase;
import br.com.iolab.commons.types.fields.Phone;
import br.com.iolab.socia.domain.assistant.instance.InstanceID;
import br.com.iolab.socia.domain.chat.ChatID;
import br.com.iolab.socia.domain.chat.message.MessageID;
import br.com.iolab.socia.domain.chat.message.types.MessageContent;
import lombok.NonNull;

public abstract class CreateMessageUseCase extends UseCase<CreateMessageUseCase.Input, CreateMessageUseCase.Output> {
    public abstract @NonNull Output perform (@NonNull Input input);

    public record Input(
            InstanceID instanceID,
            Phone from,
            Phone to,
            MessageContent content
    ) {
    }

    public record Output(
            MessageID messageID,
            ChatID chatID
    ) {
    }
}
