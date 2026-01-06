package br.com.iolab.socia.domain.assistant.knowledge;

import br.com.iolab.commons.domain.model.ModelID;
import br.com.iolab.commons.domain.utils.IDUtils;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

public class KnowledgeID extends ModelID {
    protected KnowledgeID(@NonNull final UUID value) {
        super(value);
    }

    public static @NonNull KnowledgeID from(@NonNull final UUID value) {
        return new KnowledgeID(value);
    }

    public static @NonNull KnowledgeID generate(@NonNull final Instant instant) {
        return new KnowledgeID(IDUtils.generate(instant));
    }
}
