package br.com.iolab.socia.domain.chat.fields;

import br.com.iolab.commons.types.Textual;
import lombok.NonNull;

public final class ChatAccount extends Textual {
    public ChatAccount (String value) {
        super(value);
    }

    public static @NonNull ChatAccount of (final String value) {
        return new ChatAccount(value);
    }
}
