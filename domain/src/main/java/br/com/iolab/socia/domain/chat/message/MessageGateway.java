package br.com.iolab.socia.domain.chat.message;

import br.com.iolab.commons.domain.model.ModelSearchGateway;
import br.com.iolab.socia.domain.chat.ChatID;
import lombok.NonNull;

import java.time.Instant;
import java.util.List;

public interface MessageGateway extends ModelSearchGateway<Message, MessageID, MessageSearchQuery> {
    List<Message> reserve (@NonNull Integer size, @NonNull Instant now, @NonNull Instant lease);

    List<Message> findAllByChatID (ChatID chatID);
}
