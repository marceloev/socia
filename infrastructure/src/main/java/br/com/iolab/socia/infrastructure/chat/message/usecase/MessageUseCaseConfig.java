package br.com.iolab.socia.infrastructure.chat.message.usecase;

import br.com.iolab.socia.application.chat.message.create.CreateMessageUseCase;
import br.com.iolab.socia.application.chat.message.create.CreateMessageUseCaseImpl;
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
}
