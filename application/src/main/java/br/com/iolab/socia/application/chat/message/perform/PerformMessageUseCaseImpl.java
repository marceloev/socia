package br.com.iolab.socia.application.chat.message.perform;

import br.com.iolab.commons.types.fields.Phone;
import br.com.iolab.commons.types.fields.TaxID;
import br.com.iolab.socia.domain.assistant.Assistant;
import br.com.iolab.socia.domain.assistant.types.AssistantProviderType;
import br.com.iolab.socia.domain.chat.message.MessageGateway;
import br.com.iolab.socia.domain.chat.message.MessageStrategy;
import br.com.iolab.socia.domain.organization.Organization;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PerformMessageUseCaseImpl extends PerformMessageUseCase {
    private final MessageGateway messageGateway;
    private final MessageStrategy messageStrategy;

    @Override
    protected void perform (@NonNull final PerformMessageUseCase.Input input) {
        var message = this.messageStrategy.perform(Assistant.create(
                        Organization.create("a", TaxID.of("06345774685")).successOrThrow().getId(),
                        Phone.of("+5534991940773"),
                        AssistantProviderType.GEMINI,
                        "gemini-3-flash-preview",
                        "SEJA GENTIL"
                ).successOrThrow(),
                input.message(),
                List.of()
        );
        this.create(this.messageGateway, message);
    }
}
