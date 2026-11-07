package com.fastbite.restaurantservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI restaurantServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("FastBite Restaurant Service API")
                        .version("v1")
                        .description("""
                                REST API for managing restaurants,
                                menu categories, and menu items in FastBite.
                                """));
    }
}