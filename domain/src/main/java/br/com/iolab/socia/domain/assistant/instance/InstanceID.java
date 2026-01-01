package br.com.iolab.socia.domain.assistant.instance;

import br.com.iolab.commons.domain.model.ModelID;
import br.com.iolab.commons.domain.utils.IDUtils;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

public class InstanceID extends ModelID {
    public InstanceID (@NonNull final UUID value) {
        super(value);
    }

    public static @NonNull InstanceID from (@NonNull final UUID value) {
        return new InstanceID(value);
    }

    public static @NonNull InstanceID generate (@NonNull final Instant instant) {
        return new InstanceID(IDUtils.generate(instant));
    }
}
