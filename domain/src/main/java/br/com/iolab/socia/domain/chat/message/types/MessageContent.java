package br.com.iolab.socia.domain.chat.message.types;

import br.com.iolab.commons.types.Textual;

import static br.com.iolab.commons.types.Checks.checkNot;

public class MessageContent extends Textual {
    public MessageContent (final String value) {
        super(value);
        checkNot(value().length() > 4000, "Não é permitido enviar mais do que 4000 caracteres em uma mensagem!");
    }

    public static MessageContent of (final String value) {
        return new MessageContent(value);
    }
}
