package br.com.iolab.socia.application.chat.message.receive;

import br.com.iolab.commons.domain.utils.ExceptionUtils;
import br.com.iolab.commons.types.fields.Phone;
import br.com.iolab.socia.application.chat.message.create.CreateMessageUseCase;
import br.com.iolab.socia.domain.assistant.instance.Instance;
import br.com.iolab.socia.domain.assistant.instance.InstanceGateway;
import br.com.iolab.socia.domain.chat.message.types.MessageContent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReceiveMessageWhatsAppUseCaseImpl extends ReceiveMessageWhatsAppUseCase {
    private final InstanceGateway instanceGateway;
    private final CreateMessageUseCase createMessageUseCase;

    @Override
    protected @NonNull CreateMessageUseCase.Output perform (@NonNull final ReceiveMessageWhatsAppUseCase.Input input) {
        if (!"message_received".equals(input.event())) {
            return new CreateMessageUseCase.Output(null, null);
        }

        var instance = this.instanceGateway.findById(input.id())
                .orElseThrow(ExceptionUtils.notFound(input.id(), Instance.class));

        var createMessageInput = new CreateMessageUseCase.Input(
                instance.getId(),
                Phone.of(input.chat()),
                Phone.of(instance.getAccount()),
                MessageContent.of(input.text())
        );

        return this.createMessageUseCase.perform(createMessageInput);
    }
}
