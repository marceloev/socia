package br.com.iolab.socia.domain.assistant.knowledge.fields;

import br.com.iolab.commons.types.Textual;
import lombok.NonNull;

public class KnowledgeValue extends Textual {
    public KnowledgeValue (final String value) {
        super(value);
    }

    public static @NonNull KnowledgeValue of (final String value) {
        return new KnowledgeValue(value);
    }
}
