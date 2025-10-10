package com.smartorder.orderservice.application.exception;

import java.util.UUID;

public class CatalogClientException extends RuntimeException {

    public CatalogClientException(UUID productId, Throwable cause) {
        super("Catalog unavailable for product: " + productId, cause);
    }
}

