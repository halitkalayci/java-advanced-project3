package com.smartorder.catalogservice.application.dto;

import java.time.Instant;
import java.util.UUID;

public record ProductDto(
        UUID id,
        String name,
        long unitCents,
        String currency,
        boolean active,
        Instant createdAt,
        Instant updatedAt) {}

