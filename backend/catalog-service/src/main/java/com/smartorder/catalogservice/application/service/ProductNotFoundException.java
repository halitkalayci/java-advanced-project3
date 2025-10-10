package com.smartorder.catalogservice.application.service;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(UUID id) {
        super("Product bulunamadı: " + id);
    }
}

