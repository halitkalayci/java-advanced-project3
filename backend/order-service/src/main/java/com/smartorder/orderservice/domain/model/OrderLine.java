package com.smartorder.orderservice.domain.model;

import java.util.Objects;
import java.util.UUID;

public record OrderLine(
        UUID productId,
        String productName,
        long unitCents,
        String currency,
        int quantity,
        long lineTotalCents) {

    public OrderLine {
        Objects.requireNonNull(productId, "productId must not be null");
        Objects.requireNonNull(productName, "productName must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
        if (productName.isBlank()) {
            throw new IllegalArgumentException("productName must not be blank");
        }
        if (unitCents < 0) {
            throw new IllegalArgumentException("unitCents must be non-negative");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
        if (lineTotalCents < 0) {
            throw new IllegalArgumentException("lineTotalCents must be non-negative");
        }
    }
}

