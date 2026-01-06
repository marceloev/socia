package br.com.iolab.socia.domain.assistant.knowledge.fields;

import br.com.iolab.commons.types.Textual;
import lombok.NonNull;

import static br.com.iolab.commons.types.Checks.checkNot;

public class KnowledgeKey extends Textual {
    public KnowledgeKey (final String value) {
        super(value);
        checkNot(value().length() > 255, "Key cannot be longer than 255 characters!");
    }

    public static @NonNull KnowledgeKey of (final String value) {
        return new KnowledgeKey(value);
    }
}
