package br.com.iolab.socia.application.chat.message.create;

import br.com.iolab.commons.application.UseCase;
import br.com.iolab.commons.types.fields.Phone;
import br.com.iolab.socia.domain.chat.ChatID;
import br.com.iolab.socia.domain.chat.message.MessageID;

public abstract class CreateMessageUseCase extends UseCase<CreateMessageUseCase.Input, CreateMessageUseCase.Output> {
    public record Input(
            Phone from,
            Phone to,
            String content
    ) {
    }

    public record Output(
            MessageID messageID,
            ChatID chatID
    ) {
    }
}
