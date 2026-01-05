package br.com.iolab.socia.domain.chat;

import br.com.iolab.commons.domain.model.ModelGateway;
import br.com.iolab.commons.types.fields.Phone;
import br.com.iolab.socia.domain.assistant.instance.InstanceID;
import br.com.iolab.socia.domain.chat.fields.ChatAccount;
import lombok.NonNull;

import java.util.Optional;

public interface ChatGateway extends ModelGateway<Chat, ChatID> {
    Optional<Chat> findByInstanceIDAndAccount(@NonNull InstanceID instanceID, @NonNull ChatAccount account);
}
