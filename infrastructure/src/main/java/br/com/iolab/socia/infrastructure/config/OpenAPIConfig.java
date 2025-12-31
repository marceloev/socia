package br.com.iolab.socia.infrastructure.config;

import br.com.iolab.commons.infrastructure.config.BasicOpenAPIConfig;
import br.com.iolab.socia.domain.chat.message.types.MessageContent;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig extends BasicOpenAPIConfig {

    static {
        SpringDocUtils.getConfig().replaceWithClass(MessageContent.class, String.class);
    }

    @Bean
    public OpenAPI openAPI () {
        return new OpenAPI()
                .info(new Info()
                        .title("Socia API")
                        .description("API de integração com socia")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("IOLab Tecnologia")
                                .email("o.marcelo.domingues@gmail.com")));
    }
}
