package com.smartorder.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Security configuration for Gateway.
 * <p>
 * Enables OAuth2 Resource Server with JWT support for validating tokens
 * from Keycloak. The validated JWT is then relayed to downstream services.
 * <p>
 * Public endpoints:
 * - /actuator/** (health, info)
 * <p>
 * All other endpoints require valid JWT authentication.
 */
@Configuration
@EnableWebFluxSecurity
public class TokenRelayConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        // Allow health/info probes without authentication
                        .pathMatchers("/actuator/health/**", "/actuator/info").permitAll()
                        // All other requests require a valid JWT
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        // Enable JWT-based authentication
                        .jwt(jwt -> {})
                )
                // Disable CSRF for stateless API Gateway
                .csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }
}

