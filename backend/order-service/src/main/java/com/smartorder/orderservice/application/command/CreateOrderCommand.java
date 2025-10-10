package com.smartorder.orderservice.application.command;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record CreateOrderCommand(List<OrderItemCommand> items) {

    public CreateOrderCommand {
        Objects.requireNonNull(items, "items must not be null");
    }

    public record OrderItemCommand(UUID productId, int quantity) {

        public OrderItemCommand {
            Objects.requireNonNull(productId, "productId must not be null");
        }
    }
}

