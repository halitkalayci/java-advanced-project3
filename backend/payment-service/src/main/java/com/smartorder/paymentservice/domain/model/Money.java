package com.smartorder.paymentservice.domain.model;

import java.util.Objects;

public record Money(long cents, String currency) {

    public Money {
        if (cents < 0) {
            throw new IllegalArgumentException("cents cannot be negative");
        }
        Objects.requireNonNull(currency, "currency cannot be null");
    }

    public static Money of(long cents, String currency) {
        return new Money(cents, currency);
    }
}

