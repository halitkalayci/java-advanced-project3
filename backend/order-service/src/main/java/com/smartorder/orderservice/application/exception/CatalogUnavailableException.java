package com.smartorder.orderservice.application.exception;

import java.util.UUID;

/**
 * Raised when the catalog cannot be reached (timeout, circuit open, transport
 * error). Distinct from {@code CatalogProductNotFoundException} so the API can
 * answer with 503 (transient) rather than 4xx (caller error).
 */
public class CatalogUnavailableException extends RuntimeException {

    public CatalogUnavailableException(UUID productId, Throwable cause) {
        super("Catalog temporarily unavailable for product: " + productId, cause);
    }
}
