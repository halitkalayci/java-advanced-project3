package com.smartorder.orderservice.infrastructure.persistence.jdbc;

import com.smartorder.orderservice.domain.model.Order;
import com.smartorder.orderservice.domain.model.OrderLine;
import com.smartorder.orderservice.domain.model.OrderStatus;
import com.smartorder.orderservice.domain.port.OrderRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.dao.OptimisticLockingFailureException;
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
        boolean existing = orderRepository.existsById(order.getId());

        if (existing) {
            // Order lines are immutable after creation; an existing order only
            // changes status. Update the header in place with optimistic locking
            // instead of deleting and recreating lines (which regenerated IDs).
            int updated = orderRepository.updateStatus(
                    order.getId(), order.getStatus().name(), order.getVersion());
            if (updated == 0) {
                throw new OptimisticLockingFailureException(
                        "Order " + order.getId() + " was modified concurrently (expected version "
                                + order.getVersion() + ")");
            }
        } else {
            OrderEntity entity = toEntityWithLines(order);
            orderRepository.save(entity);
        }

        return order;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return orderRepository
                .findById(id)
                .map(entity -> toDomain(entity, lineRepository.findByOrderId(id)));
    }

    private OrderEntity toEntityWithLines(Order order) {
        List<OrderLineEntity> items = toLineEntities(order);
        return new OrderEntity(
                order.getId(),
                order.getStatus().name(),
                order.getTotalCents(),
                order.getCurrency(),
                order.getCreatedAt(),
                items);
    }

    private List<OrderLineEntity> toLineEntities(Order order) {
        return order.getLines().stream()
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

        return Order.restore(
                entity.getId(),
                orderItems,
                entity.getTotalCents(),
                entity.getCurrency(),
                status,
                entity.getCreatedAt(),
                entity.getVersion());
    }
}
