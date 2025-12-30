package br.com.iolab.socia.domain.chat.message.resource;

import br.com.iolab.commons.domain.model.ModelID;
import br.com.iolab.commons.domain.utils.IDUtils;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

public class MessageResourceID extends ModelID {
    protected MessageResourceID (@NonNull final UUID value) {
        super(value);
    }

    public static @NonNull MessageResourceID from (@NonNull final UUID value) {
        return new MessageResourceID(value);
    }

    public static @NonNull MessageResourceID generate (@NonNull final Instant instant) {
        return new MessageResourceID(IDUtils.generate(instant));
    }
}
