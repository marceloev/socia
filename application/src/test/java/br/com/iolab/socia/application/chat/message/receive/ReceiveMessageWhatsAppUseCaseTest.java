package br.com.iolab.socia.application.chat.message.receive;

import br.com.iolab.socia.application.chat.message.create.CreateMessageUseCase;
import br.com.iolab.socia.domain.assistant.instance.Instance;
import br.com.iolab.socia.domain.assistant.instance.InstanceGateway;
import br.com.iolab.socia.domain.assistant.instance.InstanceID;
import br.com.iolab.socia.domain.assistant.instance.InstanceStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiveMessageWhatsAppUseCaseTest {

    @Mock
    private InstanceGateway instanceGateway;

    @Mock
    private InstanceStrategy instanceStrategy;

    @Mock
    private CreateMessageUseCase createMessageUseCase;

    @InjectMocks
    private ReceiveMessageWhatsAppUseCaseImpl useCase;

    @Test
    @DisplayName("Should successfully process received message event")
    void shouldProcessReceivedMessage() {
        // Given
        var instanceID = InstanceID.generate(Instant.now());
        var input = new ReceiveMessageWhatsAppUseCase.Input(
                instanceID,
                "message_received",
                "5511999999999",
                "Hello World",
                null
        );

        var instance = mock(Instance.class);
        when(instance.getId()).thenReturn(instanceID);
        when(instanceGateway.findById(instanceID)).thenReturn(Optional.of(instance));

        var expectedOutput = new CreateMessageUseCase.Output(null, null); // Mock output
        when(createMessageUseCase.perform(any())).thenReturn(expectedOutput);

        // When
        var result = useCase.perform(input);

        // Then
        assertThat(result).isNotNull();
        verify(instanceGateway).findById(instanceID);
        verify(createMessageUseCase).perform(any(CreateMessageUseCase.Input.class));
    }

    @Test
    @DisplayName("Should ignore events other than message_received")
    void shouldIgnoreOtherEvents() {
        // Given
        var input = new ReceiveMessageWhatsAppUseCase.Input(
                InstanceID.generate(Instant.now()),
                "message_sent",
                "5511999999999",
                "Hello",
                null
        );

        // When
        var result = useCase.perform(input);

        // Then
        assertThat(result).isNotNull();
        verifyNoInteractions(instanceGateway);
        verifyNoInteractions(createMessageUseCase);
    }
}
