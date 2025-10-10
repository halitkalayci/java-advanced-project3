package com.smartorder.notificationservice.infrastructure.messaging.stream;

import com.smartorder.notificationservice.application.handler.NotificationEventHandler;
import com.smartorder.notificationservice.domain.model.PaymentFailed;
import com.smartorder.notificationservice.domain.model.PaymentSucceeded;
import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationStreamConsumers {

    private final NotificationEventHandler handler;

    public NotificationStreamConsumers(NotificationEventHandler handler) {
        this.handler = handler;
    }

    @Bean
    public Consumer<PaymentSucceeded> paymentSucceeded() {
        return handler::onPaymentSucceeded;
    }

    @Bean
    public Consumer<PaymentFailed> paymentFailed() {
        return handler::onPaymentFailed;
    }
}

