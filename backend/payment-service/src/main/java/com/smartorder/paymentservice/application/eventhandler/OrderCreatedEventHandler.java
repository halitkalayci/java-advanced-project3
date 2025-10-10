package com.smartorder.paymentservice.application.eventhandler;

import com.smartorder.paymentservice.application.dto.OrderCreatedMessage;
import com.smartorder.paymentservice.application.service.PaymentService;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedEventHandler {

    private final PaymentService paymentService;

    public OrderCreatedEventHandler(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void handle(OrderCreatedMessage message) {
        paymentService.processOrderCreated(message);
    }
}

