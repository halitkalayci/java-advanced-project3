package com.smartorder.orderservice.application.eventhandler;

import com.smartorder.orderservice.application.dto.PaymentFailedMessage;
import com.smartorder.orderservice.application.dto.PaymentSucceededMessage;
import com.smartorder.orderservice.application.service.OrderCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class PaymentEventHandlers {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventHandlers.class);

    private final OrderCommandService orderCommandService;

    public PaymentEventHandlers(OrderCommandService orderCommandService) {
        this.orderCommandService = orderCommandService;
    }

    @Bean
    public Consumer<PaymentSucceededMessage> paymentSucceeded() {
        return message -> {
            log.info(
                    "Payment succeeded for order {}, marking as COMPLETED at {}",
                    message.orderId(),
                    message.paidAt());
            orderCommandService.handlePaymentSucceeded(message.orderId());
        };
    }

    @Bean
    public Consumer<PaymentFailedMessage> paymentFailed() {
        return message -> {
            log.warn(
                    "Payment failed for order {}: {} at {}. Marking as CANCELLED",
                    message.orderId(),
                    message.reason(),
                    message.failedAt());
            orderCommandService.handlePaymentFailed(message.orderId(), message.reason());
        };
    }
}

