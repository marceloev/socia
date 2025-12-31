package br.com.iolab.socia.infrastructure.chat.message.api;

import br.com.iolab.socia.infrastructure.chat.message.models.request.CreateMessageRequest;
import br.com.iolab.socia.infrastructure.chat.message.models.response.ListMessagesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Tag(name = "Messages", description = "Gerenciamento de mensagens")
@RequestMapping(path = "/messages")
public interface MessageAPI {

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Criar uma nova mensagem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mensagem criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor"),
    })
    ResponseEntity<?> create (@NonNull @RequestBody CreateMessageRequest request);

    @GetMapping(path = "/chat/{chatId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Listar mensagens de um chat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de mensagens retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Chat não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor"),
    })
    ResponseEntity<ListMessagesResponse> listByChat (@NonNull @PathVariable UUID chatId);
}
