package br.com.iolab.socia.application.chat.message.create;

import br.com.iolab.commons.domain.utils.ExceptionUtils;
import br.com.iolab.commons.types.Streams;
import br.com.iolab.socia.domain.assistant.Assistant;
import br.com.iolab.socia.domain.assistant.AssistantGateway;
import br.com.iolab.socia.domain.assistant.instance.Instance;
import br.com.iolab.socia.domain.assistant.instance.InstanceGateway;
import br.com.iolab.socia.domain.assistant.types.AssistantProviderType;
import br.com.iolab.socia.domain.chat.Chat;
import br.com.iolab.socia.domain.chat.ChatGateway;
import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.MessageGateway;
import br.com.iolab.socia.domain.chat.message.resource.MessageResource;
import br.com.iolab.socia.domain.chat.message.resource.MessageResourceGateway;
import br.com.iolab.socia.domain.member.Member;
import br.com.iolab.socia.domain.member.MemberGateway;
import br.com.iolab.socia.domain.member.types.MemberRoleType;
import br.com.iolab.socia.domain.organization.Organization;
import br.com.iolab.socia.domain.organization.OrganizationGateway;
import br.com.iolab.socia.domain.organization.OrganizationID;
import br.com.iolab.socia.domain.user.User;
import br.com.iolab.socia.domain.user.UserGateway;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.stream.Collectors;

import static br.com.iolab.socia.domain.chat.message.types.MessageRoleType.USER;
import static br.com.iolab.socia.domain.chat.message.types.MessageStatusType.RECEIVED;

@RequiredArgsConstructor
public class CreateMessageUseCaseImpl extends CreateMessageUseCase {
    private final UserGateway userGateway;
    private final MemberGateway memberGateway;
    private final OrganizationGateway organizationGateway;

    private final InstanceGateway instanceGateway;
    private final AssistantGateway assistantGateway;

    private final ChatGateway chatGateway;
    private final MessageGateway messageGateway;
    private final MessageResourceGateway messageResourceGateway;

    @Override
    public @NonNull Output perform (@NonNull final CreateMessageUseCase.Input input) {
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
                    "gemini-3-flash-preview",
                    "Feliz"
            ).successOrThrow();
            this.create(this.assistantGateway, createAssistant);

            chat = Chat.create(
                    createOrganization.getId(),
                    createAssistant.getId(),
                    input.instanceID(),
                    createUser.getId(),
                    input.to(),
                    input.from(),
                    1L
            ).successOrThrow();

            this.create(this.chatGateway, chat);
        } else {
            var existingChat = this.chatGateway.findByToAndFrom(input.to(), input.from());
            if (existingChat.isPresent()) {
                chat = existingChat.get()
                        .incrementMessageCount()
                        .successOrThrow();

                this.update(this.chatGateway, chat);
            } else {
                var member = this.memberGateway.findAllByUserID(existingUser.get().getId());

                var instance = this.instanceGateway.findById(input.instanceID())
                        .orElseThrow(ExceptionUtils.notFound(input.instanceID(), Instance.class));

                Assistant assistant;
                if (instance.isShowcase()) {
                    var organizationIDs = Streams.streamOf(member)
                            .map(Member::getOrganizationID)
                            .collect(Collectors.toSet());

                    var assistants = this.assistantGateway.findAllByOrganizationIDIn(organizationIDs).stream()
                            .filter(it -> it.getPhone().equals(input.to()))
                            .toList();

                    if (assistants.size() != 1) {
                        var ownerMember = Streams.streamOf(member)
                                .filter(it -> MemberRoleType.OWNER.equals(it.getRole()))
                                .findFirst();

                        OrganizationID organizationID;
                        if (ownerMember.isEmpty()) {
                            var createOrganization = Organization.create(
                                    "Empresa ".concat(input.from().value()),
                                    null
                            ).successOrThrow();
                            this.create(this.organizationGateway, createOrganization);

                            organizationID = createOrganization.getId();
                        } else {
                            organizationID = ownerMember.get().getOrganizationID();
                        }

                        assistant = Assistant.create(
                                organizationID,
                                input.to(),
                                AssistantProviderType.GEMINI,
                                "gemini-3-flash-preview",
                                "Feliz"
                        ).successOrThrow();
                        this.create(this.assistantGateway, assistant);
                    } else {
                        assistant = assistants.getFirst();
                    }
                } else {
                    assistant = this.assistantGateway.findById(instance.getAssistantID())
                            .filter(it -> member.stream().anyMatch(m -> m.getOrganizationID().equals(it.getOrganizationID())))
                            .orElseThrow(ExceptionUtils.notFound(instance.getAssistantID(), Assistant.class));
                }

                chat = Chat.create(
                        assistant.getOrganizationID(),
                        assistant.getId(),
                        instance.getId(),
                        existingUser.get().getId(),
                        input.to(),
                        input.from(),
                        1L
                ).successOrThrow();
                this.create(this.chatGateway, chat);
            }
        }

        var message = Message.create(
                chat.getId(),
                RECEIVED,
                USER,
                input.content(),
                Collections.emptyMap()
        ).successOrThrow();
        this.create(this.messageGateway, message);

        var messageResources = Streams.streamOf(input.resources())
                .map(resource -> {
                    var messageResource = MessageResource.create(
                            message.getId(),
                            resource.type(),
                            resource.contentType(),
                            resource.content()
                    );

                    return messageResource.successOrThrow();
                }).toList();
        this.create(this.messageResourceGateway, messageResources);

        return new Output(
                message.getId(),
                message.getChatID()
        );
    }
}
