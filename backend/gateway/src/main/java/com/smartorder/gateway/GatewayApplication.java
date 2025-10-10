package com.smartorder.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * SmartOrder Gateway Application.
 * <p>
 * Acts as the edge service routing traffic to internal microservices.
 * Features:
 * - Spring Cloud Gateway for routing
 * - Eureka client for service discovery
 * - OAuth2 Resource Server for JWT validation
 * - Request ID filter for distributed tracing
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}

