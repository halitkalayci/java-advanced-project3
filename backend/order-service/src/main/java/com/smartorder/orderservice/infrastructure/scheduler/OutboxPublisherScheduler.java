package com.smartorder.orderservice.infrastructure.scheduler;

import com.smartorder.orderservice.infrastructure.messaging.stream.OrderCreatedEventPayload;
import com.smartorder.orderservice.infrastructure.messaging.stream.OrderCreatedEventPublisher;
import com.smartorder.orderservice.infrastructure.persistence.jdbc.OrderOutboxEntity;
import com.smartorder.orderservice.infrastructure.persistence.jdbc.OrderOutboxRepository;
import java.time.Instant;
import java.util.List;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxPublisherScheduler {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisherScheduler.class);

    /** After this many failed attempts an event is left for manual recovery (dead-letter). */
    private static final int MAX_RETRIES = 10;
    private static final int BATCH_SIZE = 50;
    private static final int MAX_ERROR_LENGTH = 1000;

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
    @SchedulerLock(name = "order-outbox-publisher", lockAtMostFor = "PT30S", lockAtLeastFor = "PT1S")
    @Transactional
    public void publishPendingEvents() {
        List<OrderOutboxEntity> events = outboxRepository.findUnpublished(MAX_RETRIES, BATCH_SIZE);
        events.forEach(this::publishEvent);
    }

    private void publishEvent(OrderOutboxEntity entity) {
        try {
            OrderCreatedEventPayload payload = payloadMapper.map(entity);
            eventPublisher.publish(payload);
            outboxRepository.markPublished(entity.getId(), Instant.now());
        } catch (Exception ex) {
            // Don't mark as published; bump the retry counter so it is retried on
            // the next tick. Once it exceeds MAX_RETRIES it drops out of the query
            // and is left as a dead-letter for manual recovery.
            log.error("Failed to publish outbox event {}; will retry (max {})", entity.getId(), MAX_RETRIES, ex);
            outboxRepository.recordFailure(entity.getId(), truncate(ex.getMessage()));
        }
    }

    private static String truncate(String message) {
        if (message == null) {
            return null;
        }
        return message.length() <= MAX_ERROR_LENGTH ? message : message.substring(0, MAX_ERROR_LENGTH);
    }
}
