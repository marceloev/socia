package br.com.iolab.socia.application.chat.message.perform;

import br.com.iolab.commons.domain.model.Model;
import br.com.iolab.commons.domain.utils.ExceptionUtils;
import br.com.iolab.socia.domain.assistant.Assistant;
import br.com.iolab.socia.domain.assistant.AssistantGateway;
import br.com.iolab.socia.domain.assistant.knowledge.KnowledgeGateway;
import br.com.iolab.socia.domain.chat.Chat;
import br.com.iolab.socia.domain.chat.ChatGateway;
import br.com.iolab.socia.domain.chat.message.MessageGateway;
import br.com.iolab.socia.domain.chat.message.resource.MessageResource;
import br.com.iolab.socia.domain.chat.message.resource.MessageResourceGateway;
import br.com.iolab.socia.domain.chat.message.strategy.MessageStrategy;
import br.com.iolab.socia.domain.chat.message.strategy.perform.PerformMessageStrategyInput;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PerformMessageUseCaseImpl extends PerformMessageUseCase {
    private final ChatGateway chatGateway;
    private final AssistantGateway assistantGateway;
    private final KnowledgeGateway knowledgeGateway;

    private final MessageGateway messageGateway;
    private final MessageResourceGateway messageResourceGateway;
    private final MessageStrategy messageStrategy;

    @Override
    protected void perform (@NonNull final PerformMessageUseCase.Input input) {
        var chat = this.chatGateway.findById(input.chatID())
                .orElseThrow(ExceptionUtils.notFound(input.chatID(), Chat.class));

        var assistant = this.assistantGateway.findById(chat.getAssistantID())
                .orElseThrow(ExceptionUtils.notFound(chat.getAssistantID(), Assistant.class));

        var history = this.messageGateway.findAllByChatID(chat.getId());

        var messageIds = history.stream().map(Model::getId).collect(Collectors.toSet());
        var resources = this.messageResourceGateway.findAllByMessageIdIn(messageIds).stream()
                .collect(Collectors.groupingBy(MessageResource::getMessageID));

        var knowledge = this.knowledgeGateway.findByAssistant(assistant.getId());

        var performed = this.messageStrategy.perform(PerformMessageStrategyInput.builder()
                .chat(chat)
                .assistant(assistant)
                .history(history)
                .resource(resources)
                .knowledge(knowledge)
                .build()
        );

        var message = this.messageStrategy
                .withChat(chat)
                .withAssistant(assistant)
                .withHistory(history)
                .withResources(resources)
                .withKnowledge(knowledge);

        this.create(this.messageGateway, message);

        var incrementedChat = chat.incrementMessageCount().successOrThrow();
        this.update(this.chatGateway, incrementedChat);
    }
}
