package com.smartorder.orderservice.application.service;

import com.smartorder.orderservice.application.dto.OrderDto;
import com.smartorder.orderservice.domain.model.Order;
import com.smartorder.orderservice.domain.port.OrderRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OrderQueryService {

    private final OrderRepository orderRepository;

    public OrderQueryService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderDto get(UUID id) {
        Order order = orderRepository
                .findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return OrderDtoMapper.toDto(order);
    }
}

