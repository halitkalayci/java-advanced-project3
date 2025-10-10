package com.smartorder.notificationservice.application.handler;

import com.smartorder.notificationservice.application.service.EmailNotificationService;
import com.smartorder.notificationservice.domain.model.PaymentFailed;
import com.smartorder.notificationservice.domain.model.PaymentSucceeded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
            log.error("Failed to send payment success email for order: {}", event.orderId(), e);
            throw e;
        }
    }

    public void onPaymentFailed(PaymentFailed event) {
        log.warn("Processing payment failure event for order: {}", event.orderId());
        try {
            emailService.sendPaymentFailureEmail(event.orderId(), event.reason(), event.failedAt());
        } catch (Exception e) {
            log.error("Failed to send payment failure email for order: {}", event.orderId(), e);
            throw e;
        }
    }
}

