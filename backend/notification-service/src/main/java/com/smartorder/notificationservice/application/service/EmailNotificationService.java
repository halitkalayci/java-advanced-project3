package com.smartorder.notificationservice.application.service;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);
    private static final int MIN_DELAY_MS = 100;
    private static final int MAX_DELAY_MS = 500;

    public void sendPaymentSuccessEmail(UUID orderId, Instant paidAt) {
        simulateEmailDelay();
        
        log.info("📧 EMAIL SENT: Payment Success Notification");
        log.info("   ├─ Order ID: {}", orderId);
        log.info("   ├─ Paid At: {}", paidAt);
        log.info("   ├─ To: customer-{}@example.com", orderId.toString().substring(0, 8));
        log.info("   ├─ Subject: Your payment was successful!");
        log.info("   └─ Status: ✅ DELIVERED");
    }

    public void sendPaymentFailureEmail(UUID orderId, String reason, Instant failedAt) {
        simulateEmailDelay();
        
        log.warn("📧 EMAIL SENT: Payment Failure Notification");
        log.warn("   ├─ Order ID: {}", orderId);
        log.warn("   ├─ Failed At: {}", failedAt);
        log.warn("   ├─ Reason: {}", reason);
        log.warn("   ├─ To: customer-{}@example.com", orderId.toString().substring(0, 8));
        log.warn("   ├─ Subject: Payment failed - Action required");
        log.warn("   └─ Status: ⚠️  DELIVERED");
    }

    private void simulateEmailDelay() {
        try {
            int delayMs = ThreadLocalRandom.current().nextInt(MIN_DELAY_MS, MAX_DELAY_MS);
            Thread.sleep(delayMs);
            log.debug("Email sending simulated with {}ms delay", delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Email simulation interrupted", e);
        }
    }
}

