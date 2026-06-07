package com.smartorder.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * Gateway route definitions using the Java DSL.
 * <p>
 * Each business route is wrapped in a Resilience4j circuit breaker that, when
 * the upstream is failing or slow, fails fast to {@code /fallback} (503) instead
 * of piling up blocked requests. Retries handle transient blips below that.
 */
@Slf4j
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("catalog-service", r -> r
                        .path("/api/catalog/**")
                        .filters(f -> f
                                .stripPrefix(2)
                                .retry(config -> config.setRetries(3))
                                .circuitBreaker(cb -> cb.setName("catalogCb").setFallbackUri("forward:/fallback"))
                        )
                        .uri("lb://catalog-service")
                )
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .filters(f -> f
                                .stripPrefix(2)
                                .retry(config -> config.setRetries(3))
                                .circuitBreaker(cb -> cb.setName("orderCb").setFallbackUri("forward:/fallback"))
                        )
                        .uri("lb://order-service")
                )
                .route("payment-service", r -> r
                        .path("/api/payments/**")
                        .filters(f -> f
                                .stripPrefix(2)
                                .retry(config -> config.setRetries(3))
                                .circuitBreaker(cb -> cb.setName("paymentCb").setFallbackUri("forward:/fallback"))
                        )
                        .uri("lb://payment-service")
                )
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f
                                .stripPrefix(2)
                                .retry(config -> config.setRetries(3))
                                .circuitBreaker(cb -> cb.setName("notificationCb").setFallbackUri("forward:/fallback"))
                        )
                        .uri("lb://notification-service")
                )
                // Default fallback - 404
                .route("fallback", r -> r
                        .path("/no-such-route/**")
                        .filters(f -> f.setStatus(HttpStatus.NOT_FOUND))
                        .uri("no://op")
                )
                .build();
    }
}
