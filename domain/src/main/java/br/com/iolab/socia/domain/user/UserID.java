package br.com.iolab.socia.domain.user;

import br.com.iolab.commons.domain.model.ModelID;
import br.com.iolab.commons.domain.utils.IDUtils;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

public class UserID extends ModelID {
    protected UserID (@NonNull final UUID value) {
        super(value);
    }

    public static @NonNull UserID from (@NonNull final UUID value) {
        return new UserID(value);
    }

    public static @NonNull UserID generate (@NonNull final Instant instant) {
        return new UserID(IDUtils.generate(instant));
    }
}
