package br.com.iolab.socia.domain.chat.message;

import br.com.iolab.commons.domain.model.ModelID;
import br.com.iolab.commons.domain.utils.IDUtils;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

public class MessageID extends ModelID {
    protected MessageID (@NonNull final UUID value) {
        super(value);
    }

    public static @NonNull MessageID from (@NonNull final UUID value) {
        return new MessageID(value);
    }

    public static @NonNull MessageID generate (@NonNull final Instant instant) {
        return new MessageID(IDUtils.generate(instant));
    }
}
