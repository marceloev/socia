package br.com.iolab.socia.infrastructure.chat.message.usecase;

import br.com.iolab.socia.application.chat.message.create.CreateMessageUseCase;
import br.com.iolab.socia.application.chat.message.create.CreateMessageUseCaseImpl;
import br.com.iolab.socia.application.chat.message.list.ListMessagesByChatUseCase;
import br.com.iolab.socia.application.chat.message.list.ListMessagesByChatUseCaseImpl;
import br.com.iolab.socia.application.chat.message.perform.PerformMessageUseCase;
import br.com.iolab.socia.application.chat.message.perform.PerformMessageUseCaseImpl;
import br.com.iolab.socia.application.chat.message.process.ProcessMessageUseCase;
import br.com.iolab.socia.application.chat.message.process.ProcessMessageUseCaseImpl;
import br.com.iolab.socia.application.chat.message.receive.ReceiveMessageWhatsAppUseCase;
import br.com.iolab.socia.application.chat.message.receive.ReceiveMessageWhatsAppUseCaseImpl;
import br.com.iolab.socia.application.chat.message.send.SendMessageUseCase;
import br.com.iolab.socia.application.chat.message.send.SendMessageUseCaseImpl;
import br.com.iolab.socia.domain.assistant.AssistantGateway;
import br.com.iolab.socia.domain.assistant.instance.InstanceGateway;
import br.com.iolab.socia.domain.assistant.instance.InstanceStrategy;
import br.com.iolab.socia.domain.assistant.knowledge.KnowledgeGateway;
import br.com.iolab.socia.domain.chat.ChatGateway;
import br.com.iolab.socia.domain.chat.message.MessageGateway;
import br.com.iolab.socia.domain.chat.message.resource.MessageResourceGateway;
import br.com.iolab.socia.domain.chat.message.strategy.MessageStrategy;
import br.com.iolab.socia.domain.member.MemberGateway;
import br.com.iolab.socia.domain.organization.OrganizationGateway;
import br.com.iolab.socia.domain.user.UserGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MessageUseCaseConfig {
    private final UserGateway userGateway;
    private final MemberGateway memberGateway;
    private final OrganizationGateway organizationGateway;

    private final AssistantGateway assistantGateway;
    private final KnowledgeGateway knowledgeGateway;
    private final InstanceGateway instanceGateway;
    private final InstanceStrategy instanceStrategy;

    private final ChatGateway chatGateway;
    private final MessageGateway messageGateway;
    private final MessageStrategy messageStrategy;
    private final MessageResourceGateway messageResourceGateway;

    @Bean
    protected CreateMessageUseCase createMessageUseCase () {
        return new CreateMessageUseCaseImpl(
                userGateway,
                memberGateway,
                organizationGateway,
                instanceGateway,
                assistantGateway,
                chatGateway,
                messageGateway,
                messageResourceGateway
        );
    }

    @Bean
    protected ListMessagesByChatUseCase listMessagesByChatUseCase () {
        return new ListMessagesByChatUseCaseImpl(
                messageGateway
        );
    }

    @Bean
    protected PerformMessageUseCase performMessageUseCase () {
        return new PerformMessageUseCaseImpl(
                chatGateway,
                instanceGateway,
                assistantGateway,
                knowledgeGateway,
                messageGateway,
                messageResourceGateway,
                messageStrategy
        );
    }

    @Bean
    protected ProcessMessageUseCase processMessageUseCase (final PerformMessageUseCase performMessageUseCase) {
        return new ProcessMessageUseCaseImpl(
                messageGateway,
                performMessageUseCase
        );
    }

    @Bean
    protected SendMessageUseCase sendMessageUseCase () {
        return new SendMessageUseCaseImpl(
                chatGateway,
                messageGateway,
                instanceGateway,
                instanceStrategy
        );
    }

    @Bean
    protected ReceiveMessageWhatsAppUseCase receiveMessageWhatsAppUseCase (final CreateMessageUseCase createMessageUseCase) {
        return new ReceiveMessageWhatsAppUseCaseImpl(
                instanceGateway,
                instanceStrategy,
                createMessageUseCase
        );
    }
}
