package com.smartorder.orderservice.infrastructure.catalog;

import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
@EnableFeignClients(basePackages = "com.smartorder.orderservice.infrastructure.catalog")
class CatalogConfiguration {

    /**
     * Relays the caller's bearer token to catalog-service. Order creation runs
     * inside an authenticated HTTP request, so the incoming {@code Authorization}
     * header is forwarded to satisfy catalog-service's resource-server security.
     */
    @Bean
    RequestInterceptor catalogBearerTokenRelay() {
        return template -> {
            if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
                String authorization = attrs.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
                if (authorization != null && !template.headers().containsKey(HttpHeaders.AUTHORIZATION)) {
                    template.header(HttpHeaders.AUTHORIZATION, authorization);
                }
            }
        };
    }
}
