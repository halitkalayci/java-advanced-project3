package com.smartorder.gateway.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Simple per-client fixed-window rate limiter applied to every request, so a
 * single client cannot overwhelm the platform. Returns 429 when the limit is
 * exceeded.
 *
 * <p>NOTE: in-memory and per gateway instance — correct for a single gateway.
 * A horizontally scaled gateway needs a shared store (e.g. Redis
 * {@code RequestRateLimiter}); this is the documented dev/single-node default.
 */
@Component
public class RateLimitGlobalFilter implements GlobalFilter, Ordered {

    private static final int MAX_REQUESTS_PER_WINDOW = 100;
    private static final long WINDOW_MILLIS = 60_000L;

    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String clientKey = resolveClientKey(exchange);
        long now = System.currentTimeMillis();

        Window window = windows.compute(clientKey, (key, existing) -> {
            if (existing == null || now - existing.startMillis >= WINDOW_MILLIS) {
                return new Window(now);
            }
            existing.count.incrementAndGet();
            return existing;
        });

        if (window.count.get() > MAX_REQUESTS_PER_WINDOW) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    private String resolveClientKey(ServerWebExchange exchange) {
        var address = exchange.getRequest().getRemoteAddress();
        return address != null ? address.getAddress().getHostAddress() : "unknown";
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private static final class Window {
        private final long startMillis;
        private final AtomicInteger count;

        private Window(long startMillis) {
            this.startMillis = startMillis;
            this.count = new AtomicInteger(1);
        }
    }
}
