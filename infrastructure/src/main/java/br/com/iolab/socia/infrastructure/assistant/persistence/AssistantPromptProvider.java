package br.com.iolab.socia.infrastructure.assistant.persistence;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssistantPromptProvider {
    private final ResourceLoader resourceLoader;

    @Getter
    private Prompt prompt;

    @PostConstruct
    protected void loadPrompts () {
        try {
            var corePrompt = loadPromptFile("classpath:prompt/core.md");
            var instagramPrompt = loadPromptFile("classpath:prompt/instagram.md");
            var whatsappPrompt = loadPromptFile("classpath:prompt/whatsapp.md");

            log.info("Prompts carregados com sucesso - Core: {} chars, Instagram: {} chars, WhatsApp: {} chars",
                    corePrompt.length(),
                    instagramPrompt.length(),
                    whatsappPrompt.length()
            );

            this.prompt = new Prompt(corePrompt, instagramPrompt, whatsappPrompt);
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao carregar prompts", e);
        }
    }

    private @NonNull String loadPromptFile (@NonNull final String path) throws IOException {
        Resource resource = this.resourceLoader.getResource(path);
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    public record Prompt(
            String core,
            String instagram,
            String whatsapp
    ) {
    }
}
