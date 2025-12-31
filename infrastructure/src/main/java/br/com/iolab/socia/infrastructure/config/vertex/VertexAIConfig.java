package br.com.iolab.socia.infrastructure.config.vertex;

import br.com.iolab.commons.domain.exceptions.InternalErrorException;
import br.com.iolab.commons.domain.utils.StringUtils;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.rpc.StatusCode;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.HarmCategory;
import com.google.cloud.vertexai.api.PredictionServiceClient;
import com.google.cloud.vertexai.api.PredictionServiceSettings;
import com.google.cloud.vertexai.api.SafetySetting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class VertexAIConfig {

    @Lazy
    @Bean(destroyMethod = "close")
    public VertexAI vertexAI (final PredictionServiceClient predictionServiceClient) {
        Supplier<PredictionServiceClient> supplier = () -> predictionServiceClient;

        return new VertexAI.Builder()
                .setProjectId(projectId)
                .setLocation(location)
                .setPredictionClientSupplier(supplier)
                .build();
    }

    @Bean(destroyMethod = "close")
    public PredictionServiceClient predictionServiceClient (final GoogleCredentials credentials) throws IOException {
        log.info("Configurando PredictionServiceClient com timeout de {} minutos e {} tentativas de retry",
                timeoutMinutes, maxRetryAttempts);

        var predictionServiceSettings = PredictionServiceSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                // Aplica configurações de retry a todos os métodos unários
                .applyToAllUnaryMethods(unary -> {
                    unary.setRetrySettings(RetrySettings.newBuilder()
                            .setMaxAttempts(maxRetryAttempts) // Número máximo de tentativas (configurável)
                            .setInitialRetryDelayDuration(Duration.ofMillis(initialRetryDelayMs)) // Atraso inicial configurável
                            .setRetryDelayMultiplier(2.0) // Multiplicador exponencial do atraso (backoff exponencial)
                            .setMaxRetryDelayDuration(Duration.ofSeconds(maxRetryDelaySeconds)) // Atraso máximo entre tentativas
                            .setTotalTimeoutDuration(Duration.ofMinutes(timeoutMinutes)) // Tempo limite total configurável
                            .build());

                    // Configura códigos de status que devem ser retentados
                    // RESOURCE_EXHAUSTED (429) - Rate limiting
                    // UNAVAILABLE (503) - Serviço temporariamente indisponível
                    // DEADLINE_EXCEEDED (504) - Timeout, mas pode ser transiente
                    unary.setRetryableCodes(
                            StatusCode.Code.RESOURCE_EXHAUSTED,
                            StatusCode.Code.UNAVAILABLE,
                            StatusCode.Code.DEADLINE_EXCEEDED
                    );

                    return null;
                })
                .build();

        log.info("PredictionServiceClient configurado com sucesso. Retry: backoff exponencial de {}ms até {}s",
                initialRetryDelayMs, maxRetryDelaySeconds);

        return PredictionServiceClient.create(predictionServiceSettings);
    }

    /**
     * Configurações de Safety Settings otimizadas para evitar bloqueios desnecessários.
     * Permite que o modelo responda mesmo em casos limítrofes.
     */
    @Bean
    public List<SafetySetting> safetySettings () {
        log.info("Configurando Safety Settings otimizados para Gemini");

        return List.of(
                SafetySetting.newBuilder()
                        .setCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH)
                        .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_ONLY_HIGH)
                        .build(),
                SafetySetting.newBuilder()
                        .setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
                        .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_ONLY_HIGH)
                        .build(),
                SafetySetting.newBuilder()
                        .setCategory(HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
                        .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_ONLY_HIGH)
                        .build(),
                SafetySetting.newBuilder()
                        .setCategory(HarmCategory.HARM_CATEGORY_HARASSMENT)
                        .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_ONLY_HIGH)
                        .build()
        );
    }

    @Bean
    public GoogleCredentials googleCredentials () {
        log.info("Configurando credenciais para Gemini...");
        if (StringUtils.isEmpty(this.credentials)) {
            return GoogleCredentials.newBuilder().build();
        }

        byte[] decoded = Base64.getDecoder().decode(this.credentials.trim());
        try (var in = new ByteArrayInputStream(decoded)) {
            return GoogleCredentials
                    .fromStream(in)
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        } catch (Exception e) {
            throw InternalErrorException.with("Erro ao tentar instanciar crendenciais do google!", e);
        }
    }
}
