package com.smartorder.paymentservice.domain.model;

import java.time.Instant;
import java.util.Objects;

public class PaymentRequest {

    private final OrderId orderId;
    private PaymentStatus status;
    private Instant lastAttemptAt;

    private PaymentRequest(OrderId orderId, PaymentStatus status, Instant lastAttemptAt) {
        this.orderId = Objects.requireNonNull(orderId, "orderId cannot be null");
        this.status = Objects.requireNonNull(status, "status cannot be null");
        this.lastAttemptAt = lastAttemptAt;
    }

    public static PaymentRequest createPending(OrderId orderId, Instant lastAttemptAt) {
        return new PaymentRequest(orderId, PaymentStatus.PENDING, lastAttemptAt);
    }

    public static PaymentRequest restore(OrderId orderId, PaymentStatus status, Instant lastAttemptAt) {
        return new PaymentRequest(orderId, status, lastAttemptAt);
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public Instant getLastAttemptAt() {
        return lastAttemptAt;
    }

    public boolean isTerminal() {
        return status == PaymentStatus.SUCCEEDED || status == PaymentStatus.FAILED;
    }

    public void markSucceeded(Instant attemptedAt) {
        this.status = PaymentStatus.SUCCEEDED;
        this.lastAttemptAt = attemptedAt;
    }

    public void markFailed(Instant attemptedAt) {
        this.status = PaymentStatus.FAILED;
        this.lastAttemptAt = attemptedAt;
    }
}

