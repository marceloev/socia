package br.com.iolab.socia.infrastructure.assistant.instance.strategy.whatsapp.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SendWhatsappMessageResponse(
        @JsonProperty(value = "success") boolean success
) {
}
