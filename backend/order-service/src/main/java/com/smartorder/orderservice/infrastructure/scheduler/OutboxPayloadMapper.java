package com.smartorder.orderservice.infrastructure.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartorder.orderservice.infrastructure.messaging.stream.OrderCreatedEventPayload;
import com.smartorder.orderservice.infrastructure.persistence.jdbc.OrderOutboxEntity;
import org.springframework.stereotype.Component;

@Component
class OutboxPayloadMapper {

    private final ObjectMapper objectMapper;

    OutboxPayloadMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    OrderCreatedEventPayload map(OrderOutboxEntity entity) {
        try {
            return objectMapper.readValue(entity.getPayload(), OrderCreatedEventPayload.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize outbox payload", e);
        }
    }
}

