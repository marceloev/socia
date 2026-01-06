package br.com.iolab.socia.domain.assistant.knowledge.fields;

import br.com.iolab.commons.types.Textual;
import lombok.NonNull;

public class KnowledgeRationale extends Textual {
    public KnowledgeRationale (final String value) {
        super(value);
    }

    public static @NonNull KnowledgeRationale of (final String value) {
        return new KnowledgeRationale(value);
    }
}
