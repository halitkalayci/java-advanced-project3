package com.smartorder.catalogservice.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProductUpdateRequest(
        @PositiveOrZero long unitCents,
        @NotBlank @Size(min = 3, max = 3) String currency,
        @NotNull Boolean active) {}

