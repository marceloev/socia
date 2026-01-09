package br.com.iolab.socia.domain.chat;

import br.com.iolab.socia.domain.assistant.AssistantID;
import br.com.iolab.socia.domain.assistant.instance.InstanceID;
import br.com.iolab.socia.domain.chat.fields.ChatAccount;
import br.com.iolab.socia.domain.chat.types.ChatStatusType;
import br.com.iolab.socia.domain.organization.OrganizationID;
import br.com.iolab.socia.domain.user.UserID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChatTest {

    @Test
    @DisplayName("Should create a valid new Chat")
    void shouldCreateValidNewChat() {
        // Given
        var now = Instant.now();
        var organizationID = OrganizationID.generate(now);
        var assistantID = AssistantID.generate(now);
        var instanceID = InstanceID.generate(now);
        var userID = UserID.generate(now);
        var account = ChatAccount.of("5511999999999");
        var initialCount = 0L;

        // When
        var result = Chat.create(organizationID, assistantID, instanceID, userID, account, initialCount);

        // Then
        var chat = result.successOrThrow();
        assertThat(chat).isNotNull();
        assertThat(chat.getId()).isNotNull();
        assertThat(chat.getCreatedAt()).isNotNull();
        assertThat(chat.getUpdatedAt()).isNotNull();
        assertThat(chat.getStatus()).isEqualTo(ChatStatusType.CREATED);
        assertThat(chat.getCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should fail when required fields are null")
    void shouldFailWithNullFields() {
        assertThatThrownBy(() -> Chat.create(null, null, null, null, null, 0L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("não pode ser nulo");
    }

    @Test
    @DisplayName("Should increment message count correctly")
    void shouldIncrementMessageCount() {
        // Given
        var now = Instant.now();
        var chat = Chat.create(
                OrganizationID.generate(now),
                AssistantID.generate(now),
                InstanceID.generate(now),
                UserID.generate(now),
                ChatAccount.of("5511999999999"),
                0L
        ).successOrThrow();

        // When
        var updatedResult = chat.incrementMessageCount(5);

        // Then
        assertThat(updatedResult.successOrThrow().getCount()).isEqualTo(5L);
        assertThat(updatedResult.successOrThrow().getId()).isEqualTo(chat.getId()); // ID remains same
    }
}
