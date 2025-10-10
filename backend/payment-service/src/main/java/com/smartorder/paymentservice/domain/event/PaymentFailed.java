package com.smartorder.paymentservice.domain.event;

import java.time.Instant;
import java.util.UUID;

public record PaymentFailed(UUID orderId, String reason, Instant failedAt) {

    public PaymentFailed {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId cannot be null");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason cannot be null or blank");
        }
        if (failedAt == null) {
            throw new IllegalArgumentException("failedAt cannot be null");
        }
    }
}

