package com.smartorder.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * Gateway route definitions using Java DSL.
 * <p>
 * Routes:
 * - /api/catalog/** → lb://catalog-service
 * - /api/orders/** → lb://order-service
 * - /api/payments/** → lb://payment-service
 * - /api/notifications/** → lb://notification-service
 * <p>
 * All routes use Eureka-based load balancing (lb://).
 */
@Slf4j
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Catalog Service
                .route("catalog-service", r -> r
                        .path("/api/catalog/**")
                        .filters(f -> f
                                .stripPrefix(2) // Remove /api/catalog
                                .retry(config -> config.setRetries(3))
                        )
                        .uri("lb://catalog-service")
                )
                // Order Service
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .filters(f -> f
                                .stripPrefix(2) // Remove /api/orders
                                .retry(config -> config.setRetries(3))
                        )
                        .uri("lb://order-service")
                )
                // Payment Service
                .route("payment-service", r -> r
                        .path("/api/payments/**")
                        .filters(f -> f
                                .stripPrefix(2) // Remove /api/payments
                                .retry(config -> config.setRetries(3))
                        )
                        .uri("lb://payment-service")
                )
                // Notification Service
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f
                                .stripPrefix(2) // Remove /api/notifications
                                .retry(config -> config.setRetries(3))
                        )
                        .uri("lb://notification-service")
                )
                // Default fallback - 404
                .route("fallback", r -> r
                        .path("/**")
                        .filters(f -> f.setStatus(HttpStatus.NOT_FOUND))
                        .uri("no://op")
                )
                .build();
    }
}

