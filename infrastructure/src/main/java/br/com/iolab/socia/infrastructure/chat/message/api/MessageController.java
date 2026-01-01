package br.com.iolab.socia.infrastructure.chat.message.api;

import br.com.iolab.socia.application.chat.message.create.CreateMessageUseCase;
import br.com.iolab.socia.application.chat.message.list.ListMessagesByChatUseCase;
import br.com.iolab.socia.application.chat.message.receive.ReceiveMessageWhatsAppUseCase;
import br.com.iolab.socia.domain.chat.ChatID;
import br.com.iolab.socia.infrastructure.chat.message.models.request.CreateMessageRequest;
import br.com.iolab.socia.infrastructure.chat.message.models.request.CreateMessageWhatsAppRequest;
import br.com.iolab.socia.infrastructure.chat.message.models.response.CreateMessageResponse;
import br.com.iolab.socia.infrastructure.chat.message.models.response.ListMessagesResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MessageController implements MessageAPI {
    private final CreateMessageUseCase createMessageUseCase;
    private final ReceiveMessageWhatsAppUseCase receiveMessageWhatsAppUseCase;
    private final ListMessagesByChatUseCase listMessagesByChatUseCase;

    @Override
    public ResponseEntity<?> create (@NonNull final CreateMessageRequest request) {
        var response = this.createMessageUseCase.execute(request.toInput(), CreateMessageResponse::present);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> receiveWhatsapp (@NonNull final CreateMessageWhatsAppRequest request) {
        var response = this.receiveMessageWhatsAppUseCase.execute(request.toInput(), CreateMessageResponse::present);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ListMessagesResponse> listByChat (@NonNull final UUID chatId) {
        var input = new ListMessagesByChatUseCase.Input(ChatID.from(chatId));
        var response = this.listMessagesByChatUseCase.execute(input, ListMessagesResponse::present);
        return ResponseEntity.ok(response);
    }
}
