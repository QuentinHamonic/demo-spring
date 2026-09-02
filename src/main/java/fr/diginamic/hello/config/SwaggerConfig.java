package fr.diginamic.hello.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Villes")
                        .version("1.0")
                        .description("Cette API fournit des données sur les villes (CRUD et recherches).")
                        .contact(new Contact().name("Diginamic").email("contact@diginamic.fr")));
    }
}
