package com.smartorder.orderservice.application.exception;

import java.util.UUID;

public class CatalogProductInactiveException extends RuntimeException {

    public CatalogProductInactiveException(UUID id) {
        super("Product is inactive: " + id);
    }
}

