package com.smartorder.paymentservice.infrastructure.config;

import com.smartorder.paymentservice.application.dto.OrderCreatedMessage;
import com.smartorder.paymentservice.application.eventhandler.OrderCreatedEventHandler;
import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentServiceApplicationConfiguration {

    private final OrderCreatedEventHandler orderCreatedEventHandler;

    public PaymentServiceApplicationConfiguration(OrderCreatedEventHandler orderCreatedEventHandler) {
        this.orderCreatedEventHandler = orderCreatedEventHandler;
    }

    @Bean
    public Consumer<OrderCreatedMessage> orderCreated() {
        return orderCreatedEventHandler::handle;
    }
}

