package br.com.iolab.socia.infrastructure.assistant.instance.strategy.whatsapp.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SendWhatsappMessageRequest(
        @JsonProperty(value = "to") String to,
        @JsonProperty(value = "message") String message
) {
    public static SendWhatsappMessageRequest with (
            final String to,
            final String message
    ) {
        return new SendWhatsappMessageRequest(
                to,
                message
        );
    }
}
