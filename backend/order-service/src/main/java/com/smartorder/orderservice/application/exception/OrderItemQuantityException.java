package com.smartorder.orderservice.application.exception;

import java.util.UUID;

public class OrderItemQuantityException extends RuntimeException {

    public OrderItemQuantityException(UUID productId, int quantity) {
        super("Invalid quantity " + quantity + " for product " + productId);
    }
}

