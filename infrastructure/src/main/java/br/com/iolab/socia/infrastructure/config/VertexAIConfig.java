package br.com.iolab.socia.infrastructure.config;

import br.com.iolab.commons.domain.exceptions.InternalErrorException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.genai.Client;
import com.google.genai.types.ClientOptions;
import com.google.genai.types.HarmBlockThreshold;
import com.google.genai.types.HarmCategory;
import com.google.genai.types.HttpOptions;
import com.google.genai.types.HttpRetryOptions;
import com.google.genai.types.SafetySetting;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
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

    @Bean(destroyMethod = "close")
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
    }

    /**
     * Configurações de Safety Settings otimizadas para evitar bloqueios desnecessários.
     * Permite que o modelo responda mesmo em casos limítrofes.
     */
    @Bean
    public List<SafetySetting> safetySettings () {
        log.info("Configurando Safety Settings otimizados para Gemini");

        return List.of(
                SafetySetting.builder()
                        .category(HarmCategory.Known.HARM_CATEGORY_HATE_SPEECH)
                        .threshold(HarmBlockThreshold.Known.BLOCK_ONLY_HIGH)
                        .build(),
                SafetySetting.builder()
                        .category(HarmCategory.Known.HARM_CATEGORY_DANGEROUS_CONTENT)
                        .threshold(HarmBlockThreshold.Known.BLOCK_ONLY_HIGH)
                        .build(),
                SafetySetting.builder()
                        .category(HarmCategory.Known.HARM_CATEGORY_SEXUALLY_EXPLICIT)
                        .threshold(HarmBlockThreshold.Known.BLOCK_ONLY_HIGH)
                        .build(),
                SafetySetting.builder()
                        .category(HarmCategory.Known.HARM_CATEGORY_HARASSMENT)
                        .threshold(HarmBlockThreshold.Known.BLOCK_ONLY_HIGH)
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
            String projectId,
            String location,
            String credentials,
            Integer timeoutMinutes,
            Integer maxRetryAttempts,
            Integer maxRetryDelaySeconds
    ) {
        public Properties {
            checkNotBlank(projectId(), "Projeto vertexAI não pode ser vazio");
            checkNotBlank(location(), "Localização vertexAI não pode ser vazio");
            checkNotBlank(credentials(), "Credenciais vertexAI não pode ser vazio");
            checkPositive(timeoutMinutes(), "Timeout vertexAI precisa ser um número válido e positivo");
            checkPositive(maxRetryAttempts(), "Qtd. tentativas vertexAI precisa ser um número válido e positivo");
            checkPositive(maxRetryDelaySeconds(), "Máximo delay de re-tentativa vertexAI precisa ser um número válido e positivo");
        }
    }
}
