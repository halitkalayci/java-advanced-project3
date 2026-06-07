package com.smartorder.orderservice.application.service;

import com.smartorder.orderservice.api.OrderController.CreateOrderRequest;
import com.smartorder.orderservice.api.OrderController.OrderItemRequest;
import com.smartorder.orderservice.application.exception.CatalogProductInactiveException;
import com.smartorder.orderservice.application.exception.MixedCurrenciesNotAllowedException;
import com.smartorder.orderservice.application.exception.OrderItemQuantityException;
import com.smartorder.orderservice.application.port.CatalogPort;
import com.smartorder.orderservice.application.port.ProductSnapshot;
import com.smartorder.orderservice.domain.event.OrderCreated;
import com.smartorder.orderservice.domain.model.Order;
import com.smartorder.orderservice.domain.model.OrderLine;
import com.smartorder.orderservice.domain.port.DomainEventPublisher;
import com.smartorder.orderservice.domain.port.OrderRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final DomainEventPublisher eventPublisher;
    private final CatalogPort catalogPort;

    public OrderCommandService(
            OrderRepository orderRepository,
            DomainEventPublisher eventPublisher,
            CatalogPort catalogPort) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.catalogPort = catalogPort;
    }

    @Transactional
    public UUID create(CreateOrderRequest request) {
        UUID orderId = UUID.randomUUID();
        List<OrderLine> lines = request.items().stream()
                .map(this::fetchProductSnapshot)
                .collect(Collectors.toList());

        validateCurrencyConsistency(lines);
        long totalCents = lines.stream().mapToLong(OrderLine::lineTotalCents).sum();
        Instant now = Instant.now();
        Order order = Order.create(orderId, lines, lines.get(0).currency(), totalCents, now);
        orderRepository.save(order);

        OrderCreated event = new OrderCreated(order.getId(), order.getTotalCents(), order.getCurrency(), Instant.now());
        eventPublisher.publish(event);

        return order.getId();
    }

    private OrderLine fetchProductSnapshot(OrderItemRequest item) {
        if (item.quantity() <= 0) {
            throw new OrderItemQuantityException(item.productId(), item.quantity());
        }
        // catalogPort translates transport errors into application exceptions
        // (CatalogProductNotFoundException / CatalogUnavailableException).
        ProductSnapshot product = catalogPort.getProduct(item.productId());
        if (!product.active()) {
            throw new CatalogProductInactiveException(item.productId());
        }
        long lineTotal = Math.multiplyExact(product.unitCents(), item.quantity());
        return new OrderLine(
                product.id(),
                product.name(),
                product.unitCents(),
                product.currency(),
                item.quantity(),
                lineTotal);
    }

    @Transactional
    public void handlePaymentSucceeded(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.markCompleted();
        orderRepository.save(order);
    }

    @Transactional
    public void handlePaymentFailed(UUID orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.markCancelled();
        orderRepository.save(order);
    }

    private void validateCurrencyConsistency(List<OrderLine> lines) {
        String currency = lines.get(0).currency();
        boolean mixed = lines.stream().anyMatch(line -> !currency.equals(line.currency()));
        if (mixed) {
            throw new MixedCurrenciesNotAllowedException();
        }
    }
}
