package com.smartorder.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Global filter that ensures every request has an X-Request-Id header.
 * <p>
 * If the header is missing, a new UUID is generated and added to the request.
 * This header is useful for distributed tracing and log correlation across services.
 */
@Slf4j
@Component
public class RequestIdFilter implements GlobalFilter, Ordered {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Check if X-Request-Id header already exists
        String requestId = request.getHeaders().getFirst(REQUEST_ID_HEADER);
        
        if (requestId == null || requestId.isEmpty()) {
            // Generate new request ID
            requestId = UUID.randomUUID().toString();
            
            // Add header to the request
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header(REQUEST_ID_HEADER, requestId)
                    .build();
            
            exchange = exchange.mutate().request(modifiedRequest).build();
            
            log.debug("Generated new request ID: {} for path: {}", requestId, request.getPath());
        } else {
            log.debug("Using existing request ID: {} for path: {}", requestId, request.getPath());
        }
        
        // Log the incoming request with request ID
        log.info("Incoming request [{}] {} {}", 
                requestId, 
                request.getMethod(), 
                request.getPath());
        
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // Execute this filter early in the filter chain
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

