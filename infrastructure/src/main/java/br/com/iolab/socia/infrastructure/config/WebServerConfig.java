package br.com.iolab.socia.infrastructure.config;

import br.com.iolab.commons.infrastructure.config.BasicWebServerConfig;
import br.com.iolab.commons.json.Json;
import br.com.iolab.socia.infrastructure.chat.message.module.MessageModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebServerConfig extends BasicWebServerConfig {
    @Bean
    protected ObjectMapper objectMapper () {
        return Json.copyMapper()
                .registerModule(new MessageModule());
    }
}
