package br.com.iolab.socia.application.chat.message.create;

import br.com.iolab.commons.domain.exceptions.BadRequestException;
import br.com.iolab.commons.types.Streams;
import br.com.iolab.socia.domain.assistant.Assistant;
import br.com.iolab.socia.domain.assistant.AssistantGateway;
import br.com.iolab.socia.domain.assistant.types.AssistantProviderType;
import br.com.iolab.socia.domain.chat.Chat;
import br.com.iolab.socia.domain.chat.ChatGateway;
import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.MessageGateway;
import br.com.iolab.socia.domain.member.Member;
import br.com.iolab.socia.domain.member.MemberGateway;
import br.com.iolab.socia.domain.organization.Organization;
import br.com.iolab.socia.domain.organization.OrganizationGateway;
import br.com.iolab.socia.domain.user.User;
import br.com.iolab.socia.domain.user.UserGateway;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

import static br.com.iolab.socia.domain.chat.message.types.MessageRoleType.USER;
import static br.com.iolab.socia.domain.chat.message.types.MessageStatusType.RECEIVED;

@RequiredArgsConstructor
public class CreateMessageUseCaseImpl extends CreateMessageUseCase {
    private final UserGateway userGateway;
    private final MemberGateway memberGateway;
    private final OrganizationGateway organizationGateway;
    private final AssistantGateway assistantGateway;

    private final ChatGateway chatGateway;
    private final MessageGateway messageGateway;

    @Override
    protected @NonNull Output perform (@NonNull final CreateMessageUseCase.Input input) {
        Chat chat;

        var existingUser = this.userGateway.findByPhone(input.from());
        if (existingUser.isEmpty()) {
            var createUser = User.prospect(
                    input.from()
            ).successOrThrow();
            this.create(this.userGateway, createUser);

            var createOrganization = Organization.create(
                    "Empresa ".concat(input.from().value()),
                    null
            ).successOrThrow();
            this.create(this.organizationGateway, createOrganization);

            var createAssistant = Assistant.create(
                    createOrganization.getId(),
                    input.to(),
                    AssistantProviderType.GEMINI,
                    null,
                    null
            ).successOrThrow();
            this.create(this.assistantGateway, createAssistant);

            chat = Chat.create(
                    createOrganization.getId(),
                    createAssistant.getId(),
                    createUser.getId(),
                    input.to(),
                    input.from()
            ).successOrThrow();

            this.create(this.chatGateway, chat);
        } else {
            chat = this.chatGateway.findByToAndFrom(input.to(), input.from())
                    .orElseGet(() -> {
                        var member = this.memberGateway.findAllByUserID(existingUser.get().getId());

                        var organizationIDs = Streams.streamOf(member)
                                .map(Member::getOrganizationID)
                                .collect(Collectors.toSet());

                        var assistants = this.assistantGateway.findAllByOrganizationIDIn(organizationIDs).stream()
                                .filter(it -> it.getPhone().equals(input.to()))
                                .toList();

                        if (assistants.size() != 1) {
                            throw BadRequestException.with("Não foi possível identificar a assistente através do número: " + input.to().value());
                        }

                        var createChat = Chat.create(
                                assistants.getFirst().getOrganizationID(),
                                assistants.getFirst().getId(),
                                existingUser.get().getId(),
                                input.to(),
                                input.from()
                        ).successOrThrow();
                        this.create(this.chatGateway, createChat);

                        return createChat;
                    });
        }

        var message = Message.create(
                chat.getId(),
                RECEIVED,
                USER,
                input.content()
        ).successOrThrow();

        this.create(this.messageGateway, message);

        return new Output(
                message.getId(),
                message.getChatID()
        );
    }
}
