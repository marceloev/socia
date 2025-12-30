package br.com.iolab.socia.domain.chat;

import br.com.iolab.commons.domain.model.ModelID;
import br.com.iolab.commons.domain.utils.IDUtils;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

public class ChatID extends ModelID {
    protected ChatID (@NonNull final UUID value) {
        super(value);
    }

    public static @NonNull ChatID from (@NonNull final UUID value) {
        return new ChatID(value);
    }

    public static @NonNull ChatID generate (@NonNull final Instant instant) {
        return new ChatID(IDUtils.generate(instant));
    }
}
