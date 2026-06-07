package com.smartorder.notificationservice.application.handler;

import com.smartorder.notificationservice.application.service.EmailNotificationService;
import com.smartorder.notificationservice.domain.model.PaymentFailed;
import com.smartorder.notificationservice.domain.model.PaymentSucceeded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Reacts to payment events by sending notifications. Email delivery is treated
 * as best-effort: a delivery failure is logged but NOT re-thrown, so the Kafka
 * message is still acknowledged. Re-throwing here previously caused the message
 * to be retried up to {@code maxAttempts} times, sending the customer duplicate
 * emails on a transient mail-gateway hiccup. Upstream payment idempotency
 * already guarantees a single terminal event per order.
 */
@Component
public class NotificationEventHandler {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventHandler.class);
    private final EmailNotificationService emailService;

    public NotificationEventHandler(EmailNotificationService emailService) {
        this.emailService = emailService;
    }

    public void onPaymentSucceeded(PaymentSucceeded event) {
        log.info("Processing payment success event for order: {}", event.orderId());
        try {
            emailService.sendPaymentSuccessEmail(event.orderId(), event.paidAt());
        } catch (Exception e) {
            log.error("Failed to send payment success email for order: {} (giving up, not retrying)",
                    event.orderId(), e);
        }
    }

    public void onPaymentFailed(PaymentFailed event) {
        log.warn("Processing payment failure event for order: {}", event.orderId());
        try {
            emailService.sendPaymentFailureEmail(event.orderId(), event.reason(), event.failedAt());
        } catch (Exception e) {
            log.error("Failed to send payment failure email for order: {} (giving up, not retrying)",
                    event.orderId(), e);
        }
    }
}
