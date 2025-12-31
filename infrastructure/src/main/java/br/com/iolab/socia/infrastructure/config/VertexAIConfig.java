package br.com.iolab.socia.infrastructure.config;

import br.com.iolab.commons.domain.exceptions.InternalErrorException;
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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

import static br.com.iolab.commons.types.Checks.checkNotBlank;
import static br.com.iolab.commons.types.Checks.checkPositive;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(VertexAIConfig.Properties.class)
public class VertexAIConfig {
    private final Properties properties;

    /*@Bean(destroyMethod = "close")
    public Client client (@NonNull final GoogleCredentials credentials) {
        // https://github.com/googleapis/java-genai
        log.info("Configurando GenAI Client com timeout de {} min e {} retries", properties.timeoutMinutes(), properties.maxRetryAttempts());

        // 1. Configuração de Retentativa
        var retryOptions = HttpRetryOptions.builder()
                .attempts(properties.maxRetryAttempts())
                .maxDelay(properties.maxRetryDelaySeconds().doubleValue())
                .build();

        // 2. Opções de HTTP (Timeout Global e Retries)
        var httpOptions = HttpOptions.builder()
                .timeout(properties.timeoutMinutes())
                .retryOptions(retryOptions)
                .build();

        // 3. Build client options
        var clientOptions = ClientOptions.builder()
                .maxConnections(64)
                .maxConnectionsPerHost(16)
                .build();

        // 4. O Cliente Unificado
        return Client.builder()
                .vertexAI(true)
                .project(properties.projectId())
                .location(properties.location())
                .credentials(credentials)
                .httpOptions(httpOptions)
                .clientOptions(clientOptions)
                .build();
    }*/
    @Lazy
    @Bean(destroyMethod = "close")
    public VertexAI vertexAI (final PredictionServiceClient predictionServiceClient) {
        return new VertexAI.Builder()
                .setProjectId(properties.projectId())
                .setLocation(properties.location())
                .setPredictionClientSupplier(() -> predictionServiceClient)
                .build();
    }

    @Bean(destroyMethod = "close")
    public PredictionServiceClient predictionServiceClient (final GoogleCredentials credentials) throws IOException {
        log.info("Configurando PredictionServiceClient com timeout de {} minutos e {} tentativas de retry",
                properties.timeoutMinutes(), properties.maxRetryAttempts());

        var predictionServiceSettings = PredictionServiceSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                // Aplica configurações de retry a todos os métodos unários
                .applyToAllUnaryMethods(unary -> {
                    unary.setRetrySettings(RetrySettings.newBuilder()
                            .setMaxAttempts(properties.maxRetryAttempts()) // Número máximo de tentativas (configurável)
                            .setRetryDelayMultiplier(2.0) // Multiplicador exponencial do atraso (backoff exponencial)
                            .setMaxRetryDelayDuration(Duration.ofSeconds(properties.maxRetryDelaySeconds())) // Atraso máximo entre tentativas
                            .setTotalTimeoutDuration(Duration.ofMinutes(properties.timeoutMinutes())) // Tempo limite total configurável
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
                properties.maxRetryDelaySeconds(),
                properties.timeoutMinutes()
        );

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

        byte[] decoded = Base64.getDecoder().decode(properties.credentials().trim());
        try (var in = new ByteArrayInputStream(decoded)) {
            return GoogleCredentials
                    .fromStream(in)
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        } catch (Exception e) {
            throw InternalErrorException.with("Erro ao tentar instanciar credenciais do google!", e);
        }
    }

    @ConfigurationProperties(prefix = "socia.assistant.gemini")
    public record Properties(
            @Name("project-id") String projectId,
            @Name("location") String location,
            @Name("credentials") String credentials,
            @Name("timeout-minutes") Integer timeoutMinutes,
            @Name("max-retry-attempts") Integer maxRetryAttempts,
            @Name("max-retry-delay-seconds") Integer maxRetryDelaySeconds
    ) {
        public Properties {
            checkNotBlank(projectId, "Projeto vertexAI não pode ser vazio");
            checkNotBlank(location, "Localização vertexAI não pode ser vazio");
            checkNotBlank(credentials, "Credenciais vertexAI não pode ser vazio");
            checkPositive(timeoutMinutes, "Timeout vertexAI precisa ser um número válido e positivo");
            checkPositive(maxRetryAttempts, "Qtd. tentativas vertexAI precisa ser um número válido e positivo");
            checkPositive(maxRetryDelaySeconds, "Máximo delay de re-tentativa vertexAI precisa ser um número válido e positivo");
        }
    }
}
