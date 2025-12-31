package br.com.iolab.socia.domain.chat.message;

import br.com.iolab.commons.domain.model.ModelSearchGateway;
import br.com.iolab.socia.domain.chat.ChatID;
import br.com.iolab.socia.domain.chat.message.valueobject.ReservationPolicy;

import java.util.List;

public interface MessageGateway extends ModelSearchGateway<Message, MessageID, MessageSearchQuery> {
    List<Message> findAllByChatID (ChatID chatID);

    List<Message> reserve (ReservationPolicy reservationPolicy);

}
