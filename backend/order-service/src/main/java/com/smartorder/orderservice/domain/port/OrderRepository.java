package com.smartorder.orderservice.domain.port;

import com.smartorder.orderservice.domain.model.Order;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(UUID id);
}

