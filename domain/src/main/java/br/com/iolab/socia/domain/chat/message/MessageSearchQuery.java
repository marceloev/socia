package br.com.iolab.socia.domain.chat.message;

import br.com.iolab.commons.domain.model.ModelSearchQuery;
import br.com.iolab.commons.domain.pagination.Sort;
import br.com.iolab.socia.domain.chat.message.types.MessageStatusType;

import java.util.Collection;

public record MessageSearchQuery(
        long page,
        long perPage,
        String search,
        Sort sort,
        Collection<MessageStatusType> statuses
) implements ModelSearchQuery {
}
