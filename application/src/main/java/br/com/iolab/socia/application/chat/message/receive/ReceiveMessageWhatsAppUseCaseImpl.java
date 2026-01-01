package br.com.iolab.socia.application.chat.message.receive;

import br.com.iolab.commons.domain.utils.ExceptionUtils;
import br.com.iolab.commons.domain.utils.StringUtils;
import br.com.iolab.commons.types.Optionals;
import br.com.iolab.commons.types.fields.Phone;
import br.com.iolab.socia.application.chat.message.create.CreateMessageUseCase;
import br.com.iolab.socia.domain.assistant.instance.Instance;
import br.com.iolab.socia.domain.assistant.instance.InstanceGateway;
import br.com.iolab.socia.domain.assistant.instance.InstanceStrategy;
import br.com.iolab.socia.domain.chat.message.types.MessageContent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Optional;

@RequiredArgsConstructor
public class ReceiveMessageWhatsAppUseCaseImpl extends ReceiveMessageWhatsAppUseCase {
    private final InstanceGateway instanceGateway;
    private final InstanceStrategy instanceStrategy;
    private final CreateMessageUseCase createMessageUseCase;

    //Esse cara vai ser convertido em um instanceStrategy.receive(), precisa ser genérico.

    @Override
    protected @NonNull CreateMessageUseCase.Output perform (@NonNull final ReceiveMessageWhatsAppUseCase.Input input) {
        if (!"message_received".equals(input.event())) {
            return new CreateMessageUseCase.Output(null, null);
        }

        var instance = this.instanceGateway.findById(input.id())
                .orElseThrow(ExceptionUtils.notFound(input.id(), Instance.class));

        var resources = new ArrayList<CreateMessageUseCase.Input.MessageResource>();
        if (StringUtils.isNotEmpty(input.fileId())) {
            var resource = this.instanceStrategy.retrieveFile(instance, input.fileId());
            resources.add(new CreateMessageUseCase.Input.MessageResource(
                    resource.getType(),
                    resource.getContentType(),
                    resource.getFile()
            ));
        }

        return this.createMessageUseCase.perform(new CreateMessageUseCase.Input(
                instance.getId(),
                Phone.of(input.chat()),
                Phone.of(instance.getAccount()),
                Optionals.mapNullable(input.text(), MessageContent::of),
                resources
        ));
    }
}
