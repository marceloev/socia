package br.com.iolab.socia.application.chat.message.perform;

import br.com.iolab.commons.application.UnitUseCase;
import br.com.iolab.socia.domain.chat.ChatID;
import br.com.iolab.socia.domain.chat.message.Message;

public abstract class PerformMessageUseCase extends UnitUseCase<PerformMessageUseCase.Input> {
    public record Input(
            ChatID chatID
    ) {
    }
}
