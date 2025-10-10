package com.smartorder.bff.controller;

import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * BFF Controller for user information.
 * <p>
 * GET /auth/me - Returns OIDC user claims
 */
@RestController
public class BffController {

    @GetMapping("/auth/me")
    public Mono<Map<String, Object>> me(@AuthenticationPrincipal OidcUser user) {
        return Mono.just(Map.of(
                "sub", user.getSubject(),
                "username", user.getPreferredUsername(),
                "email", user.getEmail(),
                "roles", user.getAuthorities()
        ));
    }
}

