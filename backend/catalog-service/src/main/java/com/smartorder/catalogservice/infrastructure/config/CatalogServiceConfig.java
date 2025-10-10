package com.smartorder.catalogservice.infrastructure.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CatalogServiceConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}

