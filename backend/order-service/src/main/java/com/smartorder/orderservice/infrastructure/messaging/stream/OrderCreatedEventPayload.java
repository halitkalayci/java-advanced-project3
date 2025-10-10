package com.smartorder.orderservice.infrastructure.messaging.stream;

import java.util.UUID;

public record OrderCreatedEventPayload(UUID orderId, long totalCents, String currency) {}

