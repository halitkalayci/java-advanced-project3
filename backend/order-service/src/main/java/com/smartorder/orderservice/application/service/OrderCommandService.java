package com.smartorder.orderservice.application.service;

import com.smartorder.orderservice.api.OrderController.CreateOrderRequest;
import com.smartorder.orderservice.api.OrderController.OrderItemRequest;
import com.smartorder.orderservice.application.exception.CatalogClientException;
import com.smartorder.orderservice.application.exception.CatalogProductInactiveException;
import com.smartorder.orderservice.application.exception.CatalogProductNotFoundException;
import com.smartorder.orderservice.application.exception.MixedCurrenciesNotAllowedException;
import com.smartorder.orderservice.application.exception.OrderItemQuantityException;
import com.smartorder.orderservice.domain.event.OrderCreated;
import com.smartorder.orderservice.domain.model.Order;
import com.smartorder.orderservice.domain.model.OrderLine;
import com.smartorder.orderservice.domain.port.DomainEventPublisher;
import com.smartorder.orderservice.domain.port.OrderRepository;
import com.smartorder.orderservice.infrastructure.catalog.CatalogClient;
import com.smartorder.orderservice.infrastructure.catalog.CatalogProductResponse;
import com.smartorder.orderservice.infrastructure.catalog.CatalogNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final DomainEventPublisher eventPublisher;
    private final CatalogClient catalogClient;
    private final long catalogTimeoutMillis;

    public OrderCommandService(
            OrderRepository orderRepository,
            DomainEventPublisher eventPublisher,
            CatalogClient catalogClient,
            @Value("${catalog.client.timeout-millis:1000}") long catalogTimeoutMillis) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.catalogClient = catalogClient;
        this.catalogTimeoutMillis = catalogTimeoutMillis;
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
        try {
            CatalogProductResponse product = catalogClient.getProduct(item.productId());
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
        } catch (CatalogNotFoundException e) {
            throw new CatalogProductNotFoundException(item.productId());
        } catch (RuntimeException e) {
            throw new CatalogClientException(item.productId(), e);
        }
    }

    private void validateCurrencyConsistency(List<OrderLine> lines) {
        String currency = lines.get(0).currency();
        boolean mixed = lines.stream().anyMatch(line -> !currency.equals(line.currency()));
        if (mixed) {
            throw new MixedCurrenciesNotAllowedException();
        }
    }
}

