package com.smartorder.orderservice.infrastructure.messaging.stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartorder.orderservice.domain.event.OrderCreated;
import com.smartorder.orderservice.domain.port.DomainEventPublisher;
import com.smartorder.orderservice.infrastructure.persistence.jdbc.OrderOutboxEntity;
import com.smartorder.orderservice.infrastructure.persistence.jdbc.OrderOutboxRepository;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OutboxDomainEventPublisher implements DomainEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxDomainEventPublisher.class);

    private final OrderOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OutboxDomainEventPublisher(OrderOutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(OrderCreated event) {
        try {
            OrderCreatedEventPayload payload = new OrderCreatedEventPayload(
                    event.orderId(), event.totalCents(), event.currency());
            String jsonPayload = objectMapper.writeValueAsString(payload);
            OrderOutboxEntity entity = new OrderOutboxEntity(
                    UUID.randomUUID(),
                    "Order",
                    event.orderId(),
                    "OrderCreated",
                    jsonPayload,
                    Instant.now(),
                    null);
            outboxRepository.save(entity);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize OrderCreated event", e);
            throw new IllegalStateException("Failed to serialize event", e);
        }
    }
}

