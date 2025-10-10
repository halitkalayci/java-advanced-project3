package com.smartorder.orderservice.application.dto;

import java.time.Instant;
import java.util.UUID;

public record PaymentSucceededMessage(UUID orderId, Instant paidAt) {}

