package br.com.iolab.socia.domain.user;

import br.com.iolab.commons.domain.model.ModelGateway;
import br.com.iolab.commons.types.fields.Phone;
import lombok.NonNull;

import java.util.Optional;

public interface UserGateway extends ModelGateway<User, UserID> {
    Optional<User> findByPhone (@NonNull Phone phone);
}
