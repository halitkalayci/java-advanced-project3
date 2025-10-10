package com.smartorder.orderservice.domain.event;

import java.time.Instant;
import java.util.UUID;

public record OrderCreated(UUID orderId, long totalCents, String currency, Instant occurredAt) {}

