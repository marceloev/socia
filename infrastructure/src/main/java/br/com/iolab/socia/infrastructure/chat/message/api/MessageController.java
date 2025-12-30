package br.com.iolab.socia.infrastructure.chat.message.api;

import br.com.iolab.socia.application.chat.message.create.CreateMessageUseCase;
import br.com.iolab.socia.infrastructure.chat.message.models.request.CreateMessageRequest;
import br.com.iolab.socia.infrastructure.chat.message.models.response.CreateMessageResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController implements MessageAPI {
    private final CreateMessageUseCase createMessageUseCase;

    @Override
    public ResponseEntity<?> create (@NonNull final CreateMessageRequest request) {
        var response = this.createMessageUseCase.execute(request.toInput(), CreateMessageResponse::present);
        return ResponseEntity.ok(response);
    }
}
