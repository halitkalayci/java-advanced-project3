package com.smartorder.orderservice.application.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderDto(
        UUID id,
        String status,
        long totalCents,
        String currency,
        Instant createdAt,
        List<LineDto> lines) {

    public record LineDto(
            UUID productId,
            String productName,
            long unitCents,
            String currency,
            int quantity,
            long lineTotalCents) {}
}

