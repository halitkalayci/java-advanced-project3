package com.smartorder.paymentservice.domain.port;

import com.smartorder.paymentservice.domain.model.OrderId;
import com.smartorder.paymentservice.domain.model.PaymentRequest;
import com.smartorder.paymentservice.domain.model.PaymentStatus;
import java.time.Instant;
import java.util.Optional;

public interface PaymentRequestRepository {

    Optional<PaymentRequest> findByOrderId(OrderId orderId);

    /**
     * Atomically claims an order for payment processing by inserting a PENDING
     * row. Returns {@code true} only for the caller that actually inserted the
     * row; concurrent or duplicate deliveries get {@code false} and must skip
     * processing. This is the single source of idempotency for payments.
     */
    boolean claim(OrderId orderId, Instant lastAttemptAt);

    void upsert(OrderId orderId, PaymentStatus status, Instant lastAttemptAt);
}

