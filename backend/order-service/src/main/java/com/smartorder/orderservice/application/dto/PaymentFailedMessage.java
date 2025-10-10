package com.smartorder.orderservice.application.dto;

import java.time.Instant;
import java.util.UUID;

public record PaymentFailedMessage(UUID orderId, String reason, Instant failedAt) {}

