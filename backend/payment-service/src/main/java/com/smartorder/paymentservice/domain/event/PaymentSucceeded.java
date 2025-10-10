package com.smartorder.paymentservice.domain.event;

import java.time.Instant;
import java.util.UUID;

public record PaymentSucceeded(UUID orderId, Instant paidAt) {

    public PaymentSucceeded {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId cannot be null");
        }
        if (paidAt == null) {
            throw new IllegalArgumentException("paidAt cannot be null");
        }
    }
}

