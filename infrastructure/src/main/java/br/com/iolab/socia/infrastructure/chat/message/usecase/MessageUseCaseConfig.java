package br.com.iolab.socia.infrastructure.chat.message.usecase;

import br.com.iolab.socia.application.chat.message.create.CreateMessageUseCase;
import br.com.iolab.socia.application.chat.message.create.CreateMessageUseCaseImpl;
import br.com.iolab.socia.application.chat.message.list.ListMessagesByChatUseCase;
import br.com.iolab.socia.application.chat.message.list.ListMessagesByChatUseCaseImpl;
import br.com.iolab.socia.application.chat.message.perform.PerformMessageUseCase;
import br.com.iolab.socia.application.chat.message.perform.PerformMessageUseCaseImpl;
import br.com.iolab.socia.application.chat.message.process.ProcessMessageUseCase;
import br.com.iolab.socia.application.chat.message.process.ProcessMessageUseCaseImpl;
import br.com.iolab.socia.application.chat.message.send.SendMessageUseCase;
import br.com.iolab.socia.application.chat.message.send.SendMessageUseCaseImpl;
import br.com.iolab.socia.domain.assistant.AssistantGateway;
import br.com.iolab.socia.domain.chat.ChatGateway;
import br.com.iolab.socia.domain.chat.message.MessageGateway;
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

    private final ChatGateway chatGateway;
    private final MessageGateway messageGateway;

    @Bean
    protected CreateMessageUseCase createMessageUseCase () {
        return new CreateMessageUseCaseImpl(
                userGateway,
                memberGateway,
                organizationGateway,
                assistantGateway,
                chatGateway,
                messageGateway
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
                messageGateway
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
    protected SendMessageUseCase sendMessageUseCase() {
        return new SendMessageUseCaseImpl(
                messageGateway
        );
    }
}
