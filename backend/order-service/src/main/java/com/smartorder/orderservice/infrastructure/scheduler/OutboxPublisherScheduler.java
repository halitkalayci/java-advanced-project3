package com.smartorder.orderservice.infrastructure.scheduler;

import com.smartorder.orderservice.infrastructure.messaging.stream.OrderCreatedEventPayload;
import com.smartorder.orderservice.infrastructure.messaging.stream.OrderCreatedEventPublisher;
import com.smartorder.orderservice.infrastructure.persistence.jdbc.OrderOutboxEntity;
import com.smartorder.orderservice.infrastructure.persistence.jdbc.OrderOutboxRepository;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxPublisherScheduler {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisherScheduler.class);

    private final OrderOutboxRepository outboxRepository;
    private final OrderCreatedEventPublisher eventPublisher;
    private final OutboxPayloadMapper payloadMapper;

    public OutboxPublisherScheduler(
            OrderOutboxRepository outboxRepository,
            OrderCreatedEventPublisher eventPublisher,
            OutboxPayloadMapper payloadMapper) {
        this.outboxRepository = outboxRepository;
        this.eventPublisher = eventPublisher;
        this.payloadMapper = payloadMapper;
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publishPendingEvents() {
        List<OrderOutboxEntity> events = outboxRepository.findUnpublished(50);
        events.forEach(this::publishEvent);
    }

    private void publishEvent(OrderOutboxEntity entity) {
        try {
            OrderCreatedEventPayload payload = payloadMapper.map(entity);
            eventPublisher.publish(payload);
            outboxRepository.markPublished(entity.getId(), Instant.now());
        } catch (Exception ex) {
            log.error("Failed to publish outbox event {}", entity.getId(), ex);
        }
    }
}

