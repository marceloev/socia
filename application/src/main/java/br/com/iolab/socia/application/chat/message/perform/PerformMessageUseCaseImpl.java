package br.com.iolab.socia.application.chat.message.perform;

import br.com.iolab.commons.domain.utils.ExceptionUtils;
import br.com.iolab.socia.domain.assistant.Assistant;
import br.com.iolab.socia.domain.assistant.AssistantGateway;
import br.com.iolab.socia.domain.assistant.knowledge.KnowledgeGateway;
import br.com.iolab.socia.domain.chat.Chat;
import br.com.iolab.socia.domain.chat.ChatGateway;
import br.com.iolab.socia.domain.chat.message.MessageGateway;
import br.com.iolab.socia.domain.chat.message.MessageStrategy;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PerformMessageUseCaseImpl extends PerformMessageUseCase {
    private final ChatGateway chatGateway;
    private final AssistantGateway assistantGateway;
    private final KnowledgeGateway knowledgeGateway;

    private final MessageGateway messageGateway;
    private final MessageStrategy messageStrategy;

    @Override
    protected void perform (@NonNull final PerformMessageUseCase.Input input) {
        var chat = this.chatGateway.findById(input.chatID())
                .orElseThrow(ExceptionUtils.notFound(input.chatID(), Chat.class));

        var assistant = this.assistantGateway.findById(chat.getAssistantID())
                .orElseThrow(ExceptionUtils.notFound(chat.getAssistantID(), Assistant.class));

        var knowledge = this.knowledgeGateway.findByAssistant(assistant.getId());

        var message = this.messageStrategy.perform(assistant, chat);
        this.create(this.messageGateway, message);

        var incrementedChat = chat.incrementMessageCount().successOrThrow();
        this.update(this.chatGateway, incrementedChat);
    }
}
