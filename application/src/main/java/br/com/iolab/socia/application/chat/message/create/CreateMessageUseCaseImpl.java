package br.com.iolab.socia.application.chat.message.create;

import br.com.iolab.commons.domain.utils.ExceptionUtils;
import br.com.iolab.commons.types.Streams;
import br.com.iolab.commons.types.fields.Phone;
import br.com.iolab.socia.domain.assistant.Assistant;
import br.com.iolab.socia.domain.assistant.AssistantGateway;
import br.com.iolab.socia.domain.assistant.instance.Instance;
import br.com.iolab.socia.domain.assistant.instance.InstanceGateway;
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
        var existingChat = this.chatGateway.findByInstanceIDAndAccount(input.instanceID(), input.account());

        Chat chat;
        if (existingChat.isEmpty()) {
            var instance = this.instanceGateway.findById(input.instanceID())
                    .orElseThrow(ExceptionUtils.notFound(input.instanceID(), Instance.class));

            var user = switch (instance.getOrigin()) {
                case WHATSAPP -> {
                    var phone = Phone.of(input.account().value());
                    yield this.userGateway.findByPhone(phone).orElseGet(() -> {
                        var createUser = User.prospect(phone).successOrThrow();
                        this.create(this.userGateway, createUser);
                        return createUser;
                    });
                }
            };

            var member = this.memberGateway.findAllByUserID(user.getId());

            Assistant assistant;
            if (instance.isShowcase()) {
                Organization organization;
                if (member.isEmpty()) {
                    organization = Organization.create(
                            "Company ".concat(input.account().value()),
                            null
                    ).successOrThrow();
                    this.create(this.organizationGateway, organization);

                    var createMember = Member.create(
                            organization.getId(),
                            user.getId(),
                            MemberRoleType.OWNER
                    ).successOrThrow();
                    this.create(this.memberGateway, createMember);

                    assistant = Assistant.suggest(
                            organization.getId(),
                            Phone.of(input.account().value()) //Unuse
                    ).successOrThrow();
                    this.create(this.assistantGateway, assistant);
                } else {
                    var organizationIDs = Streams.streamOf(member)
                            .map(Member::getOrganizationID)
                            .collect(Collectors.toSet());

                    assistant = this.assistantGateway.findAllByOrganizationIDIn(organizationIDs).getFirst();
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
                    user.getId(),
                    input.account(),
                    1L
            ).successOrThrow();
            this.create(this.chatGateway, chat);
        } else {
            var incrementedChat = existingChat.get().incrementMessageCount().successOrThrow();
            this.update(this.chatGateway, incrementedChat);

            chat = incrementedChat;
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
