package com.smartorder.paymentservice.application.dto;

import java.util.UUID;

public record OrderCreatedMessage(UUID orderId, long totalCents, String currency) {

    public OrderCreatedMessage {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId cannot be null");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("currency cannot be null or blank");
        }
    }
}

