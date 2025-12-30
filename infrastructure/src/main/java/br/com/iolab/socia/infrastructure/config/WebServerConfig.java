package br.com.iolab.socia.infrastructure.config;

import br.com.sagessetec.commons.infrastructure.config.BasicWebServerConfig;
import br.com.sagessetec.commons.json.Json;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebServerConfig extends BasicWebServerConfig {
    @Bean
    protected ObjectMapper objectMapper () {
        return Json.copyMapper();
    }
}
