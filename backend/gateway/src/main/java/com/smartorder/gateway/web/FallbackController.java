package com.smartorder.gateway.web;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Target of the route circuit breakers. When an upstream service is failing or
 * the breaker is open, the gateway forwards here and returns a clean 503 instead
 * of leaking the raw error or blocking.
 */
@RestController
public class FallbackController {

    @RequestMapping("/fallback")
    public Mono<ResponseEntity<Map<String, Object>>> fallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "type", "service_unavailable",
                        "detail", "Upstream service is unavailable, please retry shortly")));
    }
}
