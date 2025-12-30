package br.com.iolab.socia.domain.chat;

import br.com.iolab.commons.domain.model.ModelGateway;
import br.com.iolab.commons.types.fields.Phone;
import lombok.NonNull;

import java.util.Optional;

public interface ChatGateway extends ModelGateway<Chat, ChatID> {
    Optional<Chat> findByToAndFrom (@NonNull Phone to, @NonNull Phone from);
}
