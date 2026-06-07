package com.smartorder.bff.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Minimal proxy: forwards any /api/** to the gateway, preserving method, path,
 * query, body and content negotiation headers. The access token is attached by
 * the OAuth2 filter on the load-balanced {@link WebClient}; the upstream
 * response (status + headers + body) is relayed back as-is.
 */
@RestController
@RequestMapping("/api")
public class ApiProxyController {

    private final WebClient webClient;

    public ApiProxyController(WebClient webClient) {
        this.webClient = webClient;
    }

    @RequestMapping("/**")
    public Mono<ResponseEntity<byte[]>> relay(ServerWebExchange exchange,
                                              @RequestBody(required = false) Mono<byte[]> body) {
        String downstreamPath = exchange.getRequest().getURI().getRawPath();
        // Defend against path traversal attempts before forwarding internally.
        if (downstreamPath.contains("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid path");
        }
        String query = exchange.getRequest().getURI().getRawQuery();
        String pathWithQuery = query != null ? downstreamPath + "?" + query : downstreamPath;
        HttpHeaders incoming = exchange.getRequest().getHeaders();

        return webClient
                .method(exchange.getRequest().getMethod())
                .uri("http://gateway" + pathWithQuery)
                .headers(h -> {
                    if (incoming.getContentType() != null) {
                        h.setContentType(incoming.getContentType());
                    }
                    if (!incoming.getAccept().isEmpty()) {
                        h.setAccept(incoming.getAccept());
                    }
                })
                .body(BodyInserters.fromPublisher(body, byte[].class))
                .exchangeToMono(clientResponse -> clientResponse.toEntity(byte[].class));
    }
}
