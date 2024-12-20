package com.github.therenegade.notification.manager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        OpenAPI api = new OpenAPI();
        api.info(new Info()
                .title("Notification Manager")
        );
        return api;
    }
}
