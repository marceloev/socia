package br.com.iolab.socia.infrastructure.chat.message.module;

import br.com.iolab.commons.json.module.TextualModule;
import br.com.iolab.socia.domain.chat.message.types.MessageContent;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class MessageModule extends SimpleModule {
    public MessageModule () {
        super("MessageModule");

        this.addSerializer(MessageContent.class, new TextualModule.TextualSerializer<>());
        this.addDeserializer(MessageContent.class, new TextualModule.TextualDeserializer<>(MessageContent.class));
    }
}
