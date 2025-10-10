package com.smartorder.orderservice.domain.port;

import com.smartorder.orderservice.domain.event.OrderCreated;

public interface DomainEventPublisher {

    void publish(OrderCreated event);
}

