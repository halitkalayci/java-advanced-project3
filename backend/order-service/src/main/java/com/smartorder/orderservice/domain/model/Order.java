package com.smartorder.orderservice.domain.model;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class Order {

    private final UUID id;
    private final List<OrderLine> lines;
    private final long totalCents;
    private final String currency;
    private OrderStatus status;
    private final Instant createdAt;
    /** Optimistic-locking version; 0 for a freshly created order. */
    private final long version;

    private Order(UUID id, List<OrderLine> lines, long totalCents, String currency, OrderStatus status, Instant createdAt, long version) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.lines = List.copyOf(lines);
        this.totalCents = totalCents;
        this.currency = Objects.requireNonNull(currency, "currency must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.version = version;
    }

    public static Order create(UUID id, List<OrderLine> lines, String currency, long totalCents, Instant createdAt) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("order must have at least one line");
        }
        if (totalCents < 0) {
            throw new IllegalArgumentException("totalCents must be non-negative");
        }
        String normalizedCurrency = normalizeCurrency(currency);
        return new Order(id, lines, totalCents, normalizedCurrency, OrderStatus.PENDING, createdAt, 0L);
    }

    public static Order restore(UUID id, List<OrderLine> lines, long totalCents, String currency, OrderStatus status, Instant createdAt, long version) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("order must have at least one line");
        }
        return new Order(id, lines, totalCents, currency, status, createdAt, version);
    }

    public UUID getId() {
        return id;
    }

    public List<OrderLine> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public long getTotalCents() {
        return totalCents;
    }

    public String getCurrency() {
        return currency;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public long getVersion() {
        return version;
    }

    public void markCompleted() {
        this.status = OrderStatus.COMPLETED;
    }

    public void markCancelled() {
        this.status = OrderStatus.CANCELLED;
    }

    private static String normalizeCurrency(String currency) {
        Objects.requireNonNull(currency, "currency must not be null");
        String normalized = currency.trim().toUpperCase(Locale.ROOT);
        if (normalized.length() != 3) {
            throw new IllegalArgumentException("currency must be three letters");
        }
        return normalized;
    }
}

