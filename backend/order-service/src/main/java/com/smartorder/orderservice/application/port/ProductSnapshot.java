package com.smartorder.orderservice.application.port;

import java.util.UUID;

/**
 * Application-level view of a catalog product, decoupled from the catalog
 * transport (Feign/HTTP). Returned by {@link CatalogPort}.
 */
public record ProductSnapshot(
        UUID id,
        String name,
        long unitCents,
        String currency,
        boolean active) {}
