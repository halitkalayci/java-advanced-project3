package com.smartorder.bff.controller;

import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Minimal proxy: forwards any /api/** to lb://gateway, preserving method, path and body.
 * <p>
 * Access token is automatically attached by WebClient OAuth2 filter.
 */
@RestController
@RequestMapping("/api")
public class ApiProxyController {

    private final WebClient webClient;

    public ApiProxyController(WebClient webClient) {
        this.webClient = webClient;
    }

    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, 
                                              RequestMethod.DELETE, RequestMethod.PATCH, RequestMethod.OPTIONS})
    public Mono<ResponseEntity<byte[]>> proxy(ServerWebExchange exchange,
                                              @RequestBody(required = false) Mono<byte[]> body) {
        var req = exchange.getRequest();
        HttpMethod method = req.getMethod();

        // Original path: /api/xxx -> forward to /api/xxx at GATEWAY
        String pathAfterApi = req.getPath().pathWithinApplication().value().substring("/api".length());
        String forwardPath = (pathAfterApi.isEmpty() ? "/" : pathAfterApi);

        var uriBuilder = URI.create(forwardPath);

        var headers = req.getHeaders();

        WebClient.RequestBodySpec spec = webClient
                .method(method)
                .uri(uriBuilder)
                .headers(h -> h.addAll(headers))
                .accept(MediaType.ALL);

        if (method == HttpMethod.GET || method == HttpMethod.DELETE || method == HttpMethod.OPTIONS) {
            return spec
                    .retrieve()
                    .toEntity(byte[].class);
        } else {
            return body
                    .defaultIfEmpty(new byte[0])
                    .flatMap(bytes -> spec
                            .body(BodyInserters.fromValue(bytes))
                            .retrieve()
                            .toEntity(byte[].class));
        }
    }
}

