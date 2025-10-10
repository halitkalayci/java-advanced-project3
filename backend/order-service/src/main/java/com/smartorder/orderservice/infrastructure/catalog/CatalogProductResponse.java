package com.smartorder.orderservice.infrastructure.catalog;

import java.util.UUID;

public record CatalogProductResponse(
        UUID id,
        String name,
        long unitCents,
        String currency,
        boolean active) {}

