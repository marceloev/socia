package br.com.iolab.socia.application.chat.message.list;

import br.com.iolab.commons.application.UseCase;
import br.com.iolab.socia.domain.chat.ChatID;
import br.com.iolab.socia.domain.chat.message.Message;

import java.util.List;

public abstract class ListMessagesByChatUseCase extends UseCase<ListMessagesByChatUseCase.Input, ListMessagesByChatUseCase.Output> {
    public record Input(
            ChatID chatID
    ) {
    }

    public record Output(
            List<Message> messages
    ) {
    }
}
