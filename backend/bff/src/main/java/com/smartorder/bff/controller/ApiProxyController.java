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


    @RequestMapping("/**")
    public Mono<ResponseEntity<byte[]>> relay(ServerWebExchange exchange,
                                              @RequestBody(required=false) Mono<byte[]> body) {
        URI fullPath = exchange.getRequest().getURI();
        String downstreamPath = exchange.getRequest().getURI().getPath();
        String query = exchange.getRequest().getURI().getRawQuery();

        String pathWithQuery = query != null ? downstreamPath + "?" + query : downstreamPath;
        var headers = exchange.getRequest().getHeaders();

        String fullRequestPath = "http://gateway/" + pathWithQuery;

        return webClient
                .method(exchange.getRequest().getMethod())
                .uri(fullRequestPath)
                .body(body != null ? BodyInserters.fromPublisher(body, byte[].class) : BodyInserters.empty())
                .exchangeToMono(clientResponse -> clientResponse.toEntity(byte[].class));
    }
}

