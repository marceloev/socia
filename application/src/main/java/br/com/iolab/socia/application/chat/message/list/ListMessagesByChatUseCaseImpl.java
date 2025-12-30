package br.com.iolab.socia.application.chat.message.list;

import br.com.iolab.socia.domain.chat.message.MessageGateway;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListMessagesByChatUseCaseImpl extends ListMessagesByChatUseCase {
    private final MessageGateway messageGateway;

    @Override
    protected @NonNull Output perform (@NonNull final Input input) {
        var messages = this.messageGateway.findAllByChatID(input.chatID());
        return new Output(messages);
    }
}
