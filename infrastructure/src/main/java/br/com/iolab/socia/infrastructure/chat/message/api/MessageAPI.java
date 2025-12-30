package br.com.iolab.socia.infrastructure.chat.message.api;

import br.com.iolab.socia.infrastructure.chat.message.models.request.CreateMessageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Messages", description = "Gerenciamento de mensagens")
@RequestMapping(path = "/messages")
public interface MessageAPI {

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Criar uma nova mensagem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instalação criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor"),
    })
    ResponseEntity<?> create (@NonNull @Valid @RequestBody CreateMessageRequest request);


}
