package com.smartorder.catalogservice.domain.model;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class Product {

    private final UUID id;
    private final String name;
    private final long unitCents;
    private final String currency;
    private final boolean active;
    private final Instant createdAt;
    private final Instant updatedAt;

    public Product(
            UUID id,
            String name,
            long unitCents,
            String currency,
            boolean active,
            Instant createdAt,
            Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "id zorunlu");
        this.name = requireNonBlank(name, "name zorunlu");
        this.unitCents = requireNonNegative(unitCents);
        this.currency = normalizeCurrency(currency);
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt zorunlu");
        this.updatedAt = updatedAt;
    }

    public UUID id() {
        return id;
    }

    public String name() {
        return name;
    }

    public long unitCents() {
        return unitCents;
    }

    public String currency() {
        return currency;
    }

    public boolean active() {
        return active;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public Product update(long unitCents, String currency, boolean active, Instant updatedAt) {
        return new Product(id, name, unitCents, currency, active, createdAt, Objects.requireNonNull(updatedAt, "updatedAt zorunlu"));
    }

    private static long requireNonNegative(long unitCents) {
        if (unitCents < 0) {
            throw new IllegalArgumentException("unitCents negatif olamaz");
        }
        return unitCents;
    }

    private static String normalizeCurrency(String currency) {
        Objects.requireNonNull(currency, "currency zorunlu");
        String normalized = currency.trim().toUpperCase(Locale.ROOT);
        if (normalized.length() != 3) {
            throw new IllegalArgumentException("currency 3 karakter olmalıdır");
        }
        return normalized;
    }

    private static String requireNonBlank(String value, String message) {
        Objects.requireNonNull(value, message);
        if (value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}

