package br.com.iolab.socia.application.chat.message.perform;

import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.MessageGateway;
import br.com.iolab.socia.domain.chat.message.types.MessageRoleType;
import br.com.iolab.socia.domain.chat.message.types.MessageStatusType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PerformMessageUseCaseImpl extends PerformMessageUseCase {
    private final MessageGateway messageGateway;

    @Override
    protected void perform (@NonNull final PerformMessageUseCase.Input input) {
        var message = Message.create(
                input.message().getChatID(),
                MessageStatusType.COMPLETED,
                MessageRoleType.ASSISTANT,
                "Fala meu bom"
        ).successOrThrow();

        this.create(this.messageGateway, message);
    }
}
