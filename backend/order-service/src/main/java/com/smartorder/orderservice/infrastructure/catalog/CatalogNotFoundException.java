package com.smartorder.orderservice.infrastructure.catalog;

import java.util.UUID;

public class CatalogNotFoundException extends RuntimeException {

    CatalogNotFoundException(UUID productId) {
        super("Catalog product not found: " + productId);
    }
}

