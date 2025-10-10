package com.smartorder.notificationservice.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record PaymentSucceeded(UUID orderId, Instant paidAt) {

    public PaymentSucceeded {
        Objects.requireNonNull(orderId, "orderId zorunlu");
        Objects.requireNonNull(paidAt, "paidAt zorunlu");
    }
}

