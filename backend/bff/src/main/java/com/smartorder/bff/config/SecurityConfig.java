package com.smartorder.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Minimal Security Configuration for BFF.
 * <p>
 * - OAuth2 Login with PKCE (Authorization Code flow)
 * - Public endpoints: /, /public/**, /actuator/**
 * - Everything else requires authentication
 * - No custom cookies, no custom filters
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers("/", "/public/**", "/actuator/**").permitAll()
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2Login(Customizer.withDefaults())   // PKCE handled automatically
                .oauth2Client(Customizer.withDefaults())
                .logout(l -> l.logoutUrl("/auth/logout"))
                .build();
    }
}

