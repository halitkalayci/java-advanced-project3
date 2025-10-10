package com.smartorder.paymentservice.application.service;

import com.smartorder.paymentservice.application.dto.OrderCreatedMessage;
import com.smartorder.paymentservice.domain.event.PaymentFailed;
import com.smartorder.paymentservice.domain.event.PaymentSucceeded;
import com.smartorder.paymentservice.domain.model.OrderId;
import com.smartorder.paymentservice.domain.model.PaymentRequest;
import com.smartorder.paymentservice.domain.model.PaymentStatus;
import com.smartorder.paymentservice.domain.port.PaymentEventPublisher;
import com.smartorder.paymentservice.domain.port.PaymentRequestRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private static final double SUCCESS_RATE = 0.8d;

    private final PaymentRequestRepository paymentRequestRepository;
    private final PaymentEventPublisher paymentEventPublisher;

    public PaymentService(
            PaymentRequestRepository paymentRequestRepository,
            PaymentEventPublisher paymentEventPublisher) {
        this.paymentRequestRepository = paymentRequestRepository;
        this.paymentEventPublisher = paymentEventPublisher;
    }

    @Transactional
    public void processOrderCreated(OrderCreatedMessage message) {
        OrderId orderId = OrderId.of(message.orderId());
        Instant now = Instant.now();

        Optional<PaymentRequest> existingRequest = paymentRequestRepository.findByOrderId(orderId);

        if (existingRequest.isPresent() && existingRequest.get().isTerminal()) {
            return;
        }

        paymentRequestRepository.upsert(orderId, PaymentStatus.PENDING, now);

        boolean paymentSuccess = ThreadLocalRandom.current().nextDouble() < SUCCESS_RATE;

        if (paymentSuccess) {
            handleSuccess(orderId, now);
        } else {
            handleFailure(orderId, now);
        }
    }

    private void handleSuccess(OrderId orderId, Instant occurredAt) {
        paymentRequestRepository.upsert(orderId, PaymentStatus.SUCCEEDED, occurredAt);
        paymentEventPublisher.publishPaymentSucceeded(new PaymentSucceeded(orderId.value(), occurredAt));
    }

    private void handleFailure(OrderId orderId, Instant occurredAt) {
        paymentRequestRepository.upsert(orderId, PaymentStatus.FAILED, occurredAt);
        paymentEventPublisher.publishPaymentFailed(
                new PaymentFailed(orderId.value(), "SIMULATED_FAILURE", occurredAt));
    }
}

