package br.com.iolab.socia.domain.assistant;

import br.com.iolab.commons.domain.model.ModelID;
import br.com.iolab.commons.domain.utils.IDUtils;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

public class AssistantID extends ModelID {
    protected AssistantID (@NonNull final UUID value) {
        super(value);
    }

    public static @NonNull AssistantID from (@NonNull final UUID value) {
        return new AssistantID(value);
    }

    public static @NonNull AssistantID generate (@NonNull final Instant instant) {
        return new AssistantID(IDUtils.generate(instant));
    }
}
