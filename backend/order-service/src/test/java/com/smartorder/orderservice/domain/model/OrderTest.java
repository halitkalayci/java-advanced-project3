package com.smartorder.orderservice.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderTest {

    private static OrderLine line(String currency) {
        return new OrderLine(UUID.randomUUID(), "Widget", 1000, currency, 1, 1000);
    }

    @Test
    void create_startsPending_withVersionZero() {
        Order order = Order.create(UUID.randomUUID(), List.of(line("USD")), "USD", 1000, Instant.now());

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getVersion()).isZero();
    }

    @Test
    void create_normalizesCurrency() {
        Order order = Order.create(UUID.randomUUID(), List.of(line("usd")), "usd", 1000, Instant.now());
        assertThat(order.getCurrency()).isEqualTo("USD");
    }

    @Test
    void create_rejectsEmptyLines() {
        assertThatThrownBy(() -> Order.create(UUID.randomUUID(), List.of(), "USD", 0, Instant.now()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void markCompleted_changesStatus() {
        Order order = Order.create(UUID.randomUUID(), List.of(line("USD")), "USD", 1000, Instant.now());
        order.markCompleted();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void restore_keepsVersion() {
        Order order = Order.restore(
                UUID.randomUUID(), List.of(line("USD")), 1000, "USD", OrderStatus.COMPLETED, Instant.now(), 7L);
        assertThat(order.getVersion()).isEqualTo(7L);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }
}
