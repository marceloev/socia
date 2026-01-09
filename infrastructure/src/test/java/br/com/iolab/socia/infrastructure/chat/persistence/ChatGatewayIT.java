package br.com.iolab.socia.infrastructure.chat.persistence;

import br.com.iolab.commons.types.fields.Phone;
import br.com.iolab.socia.domain.assistant.AssistantID;
import br.com.iolab.socia.domain.assistant.instance.InstanceID;
import br.com.iolab.socia.domain.chat.Chat;
import br.com.iolab.socia.domain.chat.ChatGateway;
import br.com.iolab.socia.domain.chat.fields.ChatAccount;
import br.com.iolab.socia.domain.organization.OrganizationID;
import br.com.iolab.socia.domain.user.UserID;
import br.com.iolab.socia.infrastructure.Application;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.PredictionServiceClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class, properties = "spring.main.allow-bean-definition-overriding=true")
@Testcontainers
@org.springframework.transaction.annotation.Transactional
class ChatGatewayIT {

    @MockitoBean
    private GoogleCredentials googleCredentials;

    @MockitoBean
    private VertexAI vertexAI;

    @MockitoBean
    private PredictionServiceClient predictionServiceClient;

    // Mock Scheduler to prevent background tasks from crashing the test context or interfering with DB
    @MockitoBean
    private br.com.iolab.socia.infrastructure.chat.message.scheduled.MessageScheduled messageScheduled;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
        // Dummy Base64 credentials for VertexAIConfig to avoid failure ({"type": "service_account", "project_id": "test"})
        registry.add("GOOGLE_GEMINI_CREDENTIALS", () -> "eyJ0eXBlIjogInNlcnZpY2VfYWNjb3VudCIsICJwcm9qZWN0X2lkIjogInRlc3QifQ==");
    }

    @Autowired
    private ChatGateway chatGateway;
    @Autowired
    private br.com.iolab.socia.domain.organization.OrganizationGateway organizationGateway;
    @Autowired
    private br.com.iolab.socia.domain.assistant.AssistantGateway assistantGateway;
    @Autowired
    private br.com.iolab.socia.domain.assistant.instance.InstanceGateway instanceGateway;
    @Autowired
    private br.com.iolab.socia.domain.user.UserGateway userGateway;

    @Test
    @DisplayName("Should save and find Chat by InstanceID and Account")
    void shouldSaveAndFindChat() {
        // Given
        var uniqueId = java.util.UUID.randomUUID().toString().substring(0, 8);
        var randomNum = java.util.concurrent.ThreadLocalRandom.current().nextInt(10000000, 99999999);
        var phone = Phone.of("+55119" + randomNum); // Valid random phone: +55 11 9XXXXXXXX
        
        // 1. Create Organization
        var organization = br.com.iolab.socia.domain.organization.Organization.create("Test Corp " + uniqueId, null).successOrThrow();
        organizationGateway.create(organization);
        
        // 2. Create User
        var user = br.com.iolab.socia.domain.user.User.prospect(phone).successOrThrow();
        userGateway.create(user);

        // 3. Create Assistant
        var assistant = br.com.iolab.socia.domain.assistant.Assistant.suggest(organization.getId(), phone).successOrThrow();
        assistantGateway.create(assistant);

        // 4. Create Instance
        var instance = br.com.iolab.socia.domain.assistant.instance.Instance.create(
                assistant.getId(),
                false,
                br.com.iolab.socia.domain.assistant.instance.types.InstanceOriginType.WHATSAPP,
                "ACC-" + uniqueId,
                br.com.iolab.socia.domain.assistant.instance.types.InstanceStatusType.ACTIVE
        ).successOrThrow();
        instanceGateway.create(instance);

        // 5. Create Chat
        var chat = Chat.create(
                organization.getId(),
                assistant.getId(),
                instance.getId(),
                user.getId(),
                ChatAccount.of("ACC-" + uniqueId),
                0L
        ).successOrThrow();

        // When
        chatGateway.create(chat); // Provided by ModelGateway
        var foundChat = chatGateway.findByInstanceIDAndAccount(chat.getInstanceID(), chat.getAccount());

        // Then
        assertThat(foundChat).isPresent();
        assertThat(foundChat.get().getId()).isEqualTo(chat.getId());
        assertThat(foundChat.get().getAccount()).isEqualTo(chat.getAccount());
    }
}
