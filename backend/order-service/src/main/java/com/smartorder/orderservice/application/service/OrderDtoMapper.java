package com.smartorder.orderservice.application.service;

import com.smartorder.orderservice.application.dto.OrderDto;
import com.smartorder.orderservice.domain.model.Order;
import com.smartorder.orderservice.domain.model.OrderLine;
import java.util.List;
import java.util.stream.Collectors;

final class OrderDtoMapper {

    private OrderDtoMapper() {}

    static OrderDto toDto(Order order) {
        List<OrderDto.LineDto> items = order.getLines().stream()
                .map(OrderDtoMapper::mapLine)
                .collect(Collectors.toList());
        return new OrderDto(
                order.getId(),
                order.getStatus().name(),
                order.getTotalCents(),
                order.getCurrency(),
                order.getCreatedAt(),
                items);
    }

    private static OrderDto.LineDto mapLine(OrderLine line) {
        return new OrderDto.LineDto(
                line.productId(),
                line.productName(),
                line.unitCents(),
                line.currency(),
                line.quantity(),
                line.lineTotalCents());
    }
}

