package com.smartorder.orderservice.application.exception;

import java.util.UUID;

public class CatalogProductNotFoundException extends RuntimeException {

    public CatalogProductNotFoundException(UUID id) {
        super("Unknown productId: " + id);
    }
}

