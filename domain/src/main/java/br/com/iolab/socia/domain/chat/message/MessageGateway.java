package br.com.iolab.socia.domain.chat.message;

import br.com.iolab.commons.domain.model.ModelGateway;
import br.com.iolab.socia.domain.chat.ChatID;

import java.util.List;

public interface MessageGateway extends ModelGateway<Message, MessageID> {
    List<Message> findAllByChatID (ChatID chatID);
}
