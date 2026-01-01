package br.com.iolab.socia.infrastructure.assistant.persistence;

import lombok.Getter;
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
    protected void loadPrompts() {
        try {
            Resource resource = this.resourceLoader.getResource("classpath:prompt/core.md");

            var corePrompt = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            log.info("Core prompt carregado com sucesso ({} caracteres)", corePrompt.length());

            this.prompt = new Prompt(corePrompt);
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao carregar prompts", e);
        }
    }

    public record Prompt(
            String core
    ) {
    }
}
