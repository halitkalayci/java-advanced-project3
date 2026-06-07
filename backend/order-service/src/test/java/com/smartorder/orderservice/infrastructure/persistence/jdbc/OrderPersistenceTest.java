package com.smartorder.orderservice.infrastructure.persistence.jdbc;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

/**
 * Persistence tests against the real Flyway schema on an embedded H2 database
 * (no Docker required). Lives in the same package so it can use the
 * package-private entities/repositories.
 */
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderPersistenceTest {

    @Autowired
    private OrderJdbcRepository orderRepository;
    @Autowired
    private OrderOutboxRepository outboxRepository;

    @Test
    void updateStatus_isOptimisticallyLocked() {
        UUID id = UUID.randomUUID();
        orderRepository.save(new OrderEntity(id, "PENDING", 1000, "USD", Instant.now(), List.of()));

        int firstUpdate = orderRepository.updateStatus(id, "COMPLETED", 0L);
        assertThat(firstUpdate).isEqualTo(1);

        // Stale version (0) no longer matches — the row is now at version 1.
        int staleUpdate = orderRepository.updateStatus(id, "CANCELLED", 0L);
        assertThat(staleUpdate).isZero();
    }

    @Test
    void outbox_findUnpublished_thenMarkPublished() {
        UUID eventId = UUID.randomUUID();
        outboxRepository.save(new OrderOutboxEntity(
                eventId, "Order", UUID.randomUUID(), "OrderCreated", "{}", Instant.now(), null));

        assertThat(outboxRepository.findUnpublished(10, 50))
                .extracting(OrderOutboxEntity::getId).contains(eventId);

        outboxRepository.markPublished(eventId, Instant.now());

        assertThat(outboxRepository.findUnpublished(10, 50))
                .extracting(OrderOutboxEntity::getId).doesNotContain(eventId);
    }

    @Test
    void outbox_recordFailure_excludesOnceMaxRetriesReached() {
        UUID eventId = UUID.randomUUID();
        outboxRepository.save(new OrderOutboxEntity(
                eventId, "Order", UUID.randomUUID(), "OrderCreated", "{}", Instant.now(), null));

        for (int i = 0; i < 3; i++) {
            outboxRepository.recordFailure(eventId, "boom");
        }

        // retry_count (3) is no longer < 3 → excluded as a dead-letter.
        assertThat(outboxRepository.findUnpublished(3, 50))
                .extracting(OrderOutboxEntity::getId).doesNotContain(eventId);
        // ...but a higher ceiling still sees it.
        assertThat(outboxRepository.findUnpublished(10, 50))
                .extracting(OrderOutboxEntity::getId).contains(eventId);
    }
}
