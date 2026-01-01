package br.com.iolab.socia.application.chat.message.receive;

import br.com.iolab.commons.application.UseCase;
import br.com.iolab.socia.application.chat.message.create.CreateMessageUseCase;
import br.com.iolab.socia.domain.assistant.instance.InstanceID;

public abstract class ReceiveMessageWhatsAppUseCase extends UseCase<ReceiveMessageWhatsAppUseCase.Input, CreateMessageUseCase.Output> {
    public record Input(
            InstanceID id,
            String event,
            String chat,
            String text,
            String fileId
    ) {
    }
}
