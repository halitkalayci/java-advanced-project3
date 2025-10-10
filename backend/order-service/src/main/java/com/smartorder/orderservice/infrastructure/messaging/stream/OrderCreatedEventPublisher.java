package com.smartorder.orderservice.infrastructure.messaging.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedEventPublisher.class);
    private static final String BINDING_NAME = "orderCreated-out-0";

    private final StreamBridge streamBridge;

    public OrderCreatedEventPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publish(OrderCreatedEventPayload payload) {
        log.info("Publishing OrderCreated event for order {}", payload.orderId());
        streamBridge.send(BINDING_NAME, payload);
    }
}

