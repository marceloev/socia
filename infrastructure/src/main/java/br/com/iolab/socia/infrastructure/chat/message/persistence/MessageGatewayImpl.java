package br.com.iolab.socia.infrastructure.chat.message.persistence;

import br.com.iolab.commons.infrastructure.persistence.BasicModelGateway;
import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.MessageGateway;
import br.com.iolab.socia.domain.chat.message.MessageID;
import br.com.iolab.infrastructure.jooq.generated.tables.records.MessagesRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static br.com.iolab.infrastructure.jooq.generated.tables.Messages.MESSAGES;

@Repository
public class MessageGatewayImpl extends BasicModelGateway<Message, MessageID, MessagesRecord> implements MessageGateway {
    protected MessageGatewayImpl (
            final DSLContext readOnlyDSLContext,
            final DSLContext writeOnlyDSLContext,
            final MessageMapperImpl messageMapper
    ) {
        super(
                readOnlyDSLContext,
                writeOnlyDSLContext,
                messageMapper,
                MESSAGES,
                MESSAGES.ID,
                MESSAGES.UPDATED_AT
        );
    }
}
