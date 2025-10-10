package com.smartorder.paymentservice.domain.port;

import com.smartorder.paymentservice.domain.model.OrderId;
import com.smartorder.paymentservice.domain.model.PaymentRequest;
import com.smartorder.paymentservice.domain.model.PaymentStatus;
import java.time.Instant;
import java.util.Optional;

public interface PaymentRequestRepository {

    Optional<PaymentRequest> findByOrderId(OrderId orderId);

    void upsert(OrderId orderId, PaymentStatus status, Instant lastAttemptAt);
}

