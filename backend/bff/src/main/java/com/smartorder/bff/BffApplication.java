package com.smartorder.bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * SmartOrder Backend For Frontend (BFF) Application.
 * <p>
 * Acts as an OAuth2 Client using Authorization Code + PKCE flow.
 * Features:
 * - Keycloak authentication (PUBLIC client, no client_secret)
 * - HttpOnly cookie session management for tokens
 * - Token relay to internal Gateway
 * - Endpoints: /auth/login, /auth/callback, /auth/logout, /auth/me
 * - Proxy: /api/** -> lb://gateway with Bearer token
 * - Eureka client for service discovery
 */
@SpringBootApplication
@EnableDiscoveryClient
public class BffApplication {

    public static void main(String[] args) {
        SpringApplication.run(BffApplication.class, args);
    }
}

