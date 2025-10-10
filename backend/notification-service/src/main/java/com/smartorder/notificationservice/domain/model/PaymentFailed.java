package com.smartorder.notificationservice.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record PaymentFailed(UUID orderId, String reason, Instant failedAt) {

    public PaymentFailed {
        Objects.requireNonNull(orderId, "orderId zorunlu");
        Objects.requireNonNull(reason, "reason zorunlu");
        Objects.requireNonNull(failedAt, "failedAt zorunlu");
    }
}

