package com.smartorder.bff.controller;

import java.util.List;
import java.util.Map;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * BFF Controller for user information and debugging.
 * <p>
 * GET /auth/me - Returns OIDC user claims
 * GET /public/services - Returns list of discovered services (DEBUG)
 */
@RestController
public class BffController {

    private final DiscoveryClient discoveryClient;

    public BffController(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @GetMapping("/auth/me")
    public Mono<Map<String, Object>> me(@AuthenticationPrincipal OidcUser user) {
        return Mono.just(Map.of(
                "sub", user.getSubject(),
                "username", user.getPreferredUsername(),
                "email", user.getEmail(),
                "roles", user.getAuthorities()
        ));
    }

    @GetMapping("/public/services")
    public Mono<Map<String, Object>> services() {
        List<String> serviceNames = discoveryClient.getServices();
        Map<String, List<ServiceInstance>> instances = serviceNames.stream()
                .collect(java.util.stream.Collectors.toMap(
                        name -> name,
                        discoveryClient::getInstances
                ));
        
        return Mono.just(Map.of(
                "services", serviceNames,
                "instances", instances
        ));
    }
}

