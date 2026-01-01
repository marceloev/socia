package br.com.iolab.socia.infrastructure.assistant.instance.strategy.whatsapp.client;

import br.com.iolab.commons.domain.exceptions.BadRequestException;
import br.com.iolab.commons.http.ConcurrencyStats;
import br.com.iolab.commons.http.HttpClient;
import br.com.iolab.commons.http.Request;
import br.com.iolab.commons.http.retry.RetryPolicy;
import br.com.iolab.socia.domain.chat.message.resource.MessageResource;
import br.com.iolab.socia.infrastructure.assistant.instance.strategy.whatsapp.enums.FileContentType;
import br.com.iolab.socia.infrastructure.assistant.instance.strategy.whatsapp.request.SendWhatsappMessageRequest;
import br.com.iolab.socia.infrastructure.assistant.instance.strategy.whatsapp.response.SendWhatsappMessageResponse;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static br.com.iolab.commons.types.Checks.checkNotBlank;

@Component
@EnableConfigurationProperties(WhatsappClient.Properties.class)
public class WhatsappClient implements HttpClient {
    private final Properties properties;
    private final HttpClient delegate;

    public WhatsappClient (final Properties properties) {
        this.properties = properties;
        this.delegate = HttpClient.builder()
                .maxConnections(20)
                .maxConcurrentRequests(100)
                .concurrencyTimeout(Duration.ofMinutes(1))
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofMinutes(1))
                .callTimeout(Duration.ofMinutes(1))
                .retryPolicy(RetryPolicy.builder()
                        .maxAttempts(3)
                        .initialDelay(Duration.ofSeconds(1))
                        .maxDelay(Duration.ofSeconds(30))
                        .multiplier(2.0)
                        .retryOnServerError()
                        .build()
                )
                .enableLogging(false)
                .build();
    }

    @Override
    public Request newRequest () {
        return this.delegate.newRequest()
                .url(properties.apiUrl())
                .header("Accept", "application/json")
                .header("Content-Type", "application/json");
    }

    public void send (
            @NonNull final String sessionId,
            @NonNull final SendWhatsappMessageRequest request
    ) {
        var response = this.newRequest()
                .url(properties.apiUrl().concat("/sessions/{sessionId}/send"))
                .pathParam("sessionId", sessionId)
                .post(request)
                .execute(SendWhatsappMessageResponse.class)
                .orElseThrow();

        if (!response.isSuccessful() || !response.requireBody().success()) {
            throw BadRequestException.with("Não foi possível enviar a mensagem!");
        }
    }

    public MessageResource retrieveFile (@NonNull final String fileId) {
        var fileType = FileContentType.fromFileName(fileId);

        var bytea = this.newRequest()
                .url(properties.apiUrl().concat("/files/{fileId}"))
                .pathParam("fileId", fileId)
                .get()
                .execute(byte[].class)
                .orElseThrow()
                .requireBody();

        return MessageResource.temporary(
                fileType.getResourceType(),
                fileType.getValue(),
                bytea
        );
    }

    @Override
    public ConcurrencyStats getConcurrencyStats () {
        return this.delegate.getConcurrencyStats();
    }

    @ConfigurationProperties(prefix = "socia.whatsapp.client")
    public record Properties(
            @Name("api-url") String apiUrl
    ) {
        public Properties {
            checkNotBlank(apiUrl, "URL da api do whatsapp não pode ser vazio");
        }
    }
}
