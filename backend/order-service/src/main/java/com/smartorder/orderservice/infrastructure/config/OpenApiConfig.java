package com.smartorder.orderservice.infrastructure.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI orderServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("SmartOrder - Order Service API")
                        .description("Order Service DDD + Hexagonal REST API")
                        .version("v1")
                        .contact(new Contact().name("SmartOrder Team").email("support@smartorder.com"))
                        .license(new License().name("Apache 2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("SmartOrder Documentation")
                        .url("https://smartorder.example/docs"));
    }
}
