package com.smartorder.notificationservice.application.service;

import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Simulated email delivery. Emits structured, single-line, parser-friendly logs
 * (no emoji / multi-line art that log aggregators can't index) and does not
 * block the caller — a real implementation would hand off to an async mail
 * gateway here.
 */
@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    public void sendPaymentSuccessEmail(UUID orderId, Instant paidAt) {
        log.info("email_sent type=payment_success orderId={} paidAt={} to=customer-{}@example.com",
                orderId, paidAt, recipientId(orderId));
    }

    public void sendPaymentFailureEmail(UUID orderId, String reason, Instant failedAt) {
        log.warn("email_sent type=payment_failure orderId={} failedAt={} reason=\"{}\" to=customer-{}@example.com",
                orderId, failedAt, reason, recipientId(orderId));
    }

    private static String recipientId(UUID orderId) {
        return orderId.toString().substring(0, 8);
    }
}
