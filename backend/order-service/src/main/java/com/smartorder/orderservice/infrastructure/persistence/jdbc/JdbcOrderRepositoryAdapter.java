package com.smartorder.orderservice.infrastructure.persistence.jdbc;

import com.smartorder.orderservice.domain.model.Order;
import com.smartorder.orderservice.domain.model.OrderLine;
import com.smartorder.orderservice.domain.model.OrderStatus;
import com.smartorder.orderservice.domain.port.OrderRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class JdbcOrderRepositoryAdapter implements OrderRepository {

    private final OrderJdbcRepository orderRepository;
    private final OrderLineJdbcRepository lineRepository;

    public JdbcOrderRepositoryAdapter(OrderJdbcRepository orderRepository, OrderLineJdbcRepository lineRepository) {
        this.orderRepository = orderRepository;
        this.lineRepository = lineRepository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = toEntity(order);
        orderRepository.save(entity);
        lineRepository.deleteAll(lineRepository.findByOrderId(order.getId()));
        lineRepository.saveAll(entity.getLines());
        return order;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return orderRepository
                .findById(id)
                .map(entity -> toDomain(entity, lineRepository.findByOrderId(id)));
    }

    private OrderEntity toEntity(Order order) {
        List<OrderLineEntity> items = order.getLines().stream()
                .map(item -> new OrderLineEntity(
                        UUID.randomUUID(),
                        order.getId(),
                        item.productId(),
                        item.productName(),
                        item.unitCents(),
                        item.currency(),
                        item.quantity(),
                        item.lineTotalCents()))
                .collect(Collectors.toList());

        return new OrderEntity(
                order.getId(),
                order.getStatus().name(),
                order.getTotalCents(),
                order.getCurrency(),
                order.getCreatedAt(),
                items);
    }

    private Order toDomain(OrderEntity entity, List<OrderLineEntity> items) {
        entity.markAsNotNew();
        items.forEach(OrderLineEntity::markAsNotNew);
        
        List<OrderLine> orderItems = items.stream()
                .map(item -> new OrderLine(
                        item.getProductId(),
                        item.getProductName(),
                        item.getUnitCents(),
                        item.getCurrency(),
                        item.getQuantity(),
                        item.getLineTotalCents()))
                .collect(Collectors.toList());

        OrderStatus status = OrderStatus.valueOf(entity.getStatus());

        return Order.restore(entity.getId(), orderItems, entity.getTotalCents(), entity.getCurrency(), status, entity.getCreatedAt());
    }
}

