package com.smartorder.paymentservice.domain.model;

import java.util.Objects;
import java.util.UUID;

public record OrderId(UUID value) {

    public OrderId {
        Objects.requireNonNull(value, "orderId value cannot be null");
    }

    public static OrderId of(UUID value) {
        return new OrderId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

