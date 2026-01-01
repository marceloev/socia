package br.com.iolab.socia.domain.chat.message.resource;

import br.com.iolab.commons.domain.model.ModelGateway;
import br.com.iolab.socia.domain.chat.message.MessageID;

import java.util.List;
import java.util.Set;

public interface MessageResourceGateway extends ModelGateway<MessageResource, MessageResourceID> {
    List<MessageResource> findAllByIdIn (Set<MessageResourceID> ids);
    List<MessageResource> findAllByMessageIdIn (Set<MessageID> ids);
}
