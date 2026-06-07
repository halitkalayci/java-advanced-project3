package com.smartorder.orderservice.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.smartorder.orderservice.api.OrderController.CreateOrderRequest;
import com.smartorder.orderservice.api.OrderController.OrderItemRequest;
import com.smartorder.orderservice.application.exception.CatalogProductInactiveException;
import com.smartorder.orderservice.application.exception.CatalogProductNotFoundException;
import com.smartorder.orderservice.application.exception.CatalogUnavailableException;
import com.smartorder.orderservice.application.exception.MixedCurrenciesNotAllowedException;
import com.smartorder.orderservice.application.exception.OrderItemQuantityException;
import com.smartorder.orderservice.application.port.CatalogPort;
import com.smartorder.orderservice.application.port.ProductSnapshot;
import com.smartorder.orderservice.domain.event.OrderCreated;
import com.smartorder.orderservice.domain.model.Order;
import com.smartorder.orderservice.domain.port.DomainEventPublisher;
import com.smartorder.orderservice.domain.port.OrderRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderCommandServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private DomainEventPublisher eventPublisher;
    @Mock
    private CatalogPort catalogPort;
    @InjectMocks
    private OrderCommandService service;

    private final UUID productId = UUID.randomUUID();

    private static ProductSnapshot snapshot(UUID id, String currency, boolean active) {
        return new ProductSnapshot(id, "Laptop", 1000, currency, active);
    }

    @Test
    void create_persistsOrderAndPublishesEvent() {
        when(catalogPort.getProduct(productId)).thenReturn(snapshot(productId, "USD", true));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UUID id = service.create(new CreateOrderRequest(List.of(new OrderItemRequest(productId, 2))));

        assertThat(id).isNotNull();
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publish(any(OrderCreated.class));
    }

    @Test
    void create_unknownProduct_propagatesNotFound_andPublishesNothing() {
        when(catalogPort.getProduct(productId)).thenThrow(new CatalogProductNotFoundException(productId));

        assertThatThrownBy(() -> service.create(new CreateOrderRequest(List.of(new OrderItemRequest(productId, 1)))))
                .isInstanceOf(CatalogProductNotFoundException.class);

        verifyNoInteractions(eventPublisher);
    }

    @Test
    void create_inactiveProduct_rejected() {
        when(catalogPort.getProduct(productId)).thenReturn(snapshot(productId, "USD", false));

        assertThatThrownBy(() -> service.create(new CreateOrderRequest(List.of(new OrderItemRequest(productId, 1)))))
                .isInstanceOf(CatalogProductInactiveException.class);
    }

    @Test
    void create_catalogUnavailable_propagates() {
        when(catalogPort.getProduct(productId))
                .thenThrow(new CatalogUnavailableException(productId, new RuntimeException("timeout")));

        assertThatThrownBy(() -> service.create(new CreateOrderRequest(List.of(new OrderItemRequest(productId, 1)))))
                .isInstanceOf(CatalogUnavailableException.class);
    }

    @Test
    void create_nonPositiveQuantity_rejectedBeforeCatalogCall() {
        assertThatThrownBy(() -> service.create(new CreateOrderRequest(List.of(new OrderItemRequest(productId, 0)))))
                .isInstanceOf(OrderItemQuantityException.class);

        verifyNoInteractions(catalogPort);
    }

    @Test
    void create_mixedCurrencies_rejected() {
        UUID second = UUID.randomUUID();
        when(catalogPort.getProduct(productId)).thenReturn(snapshot(productId, "USD", true));
        when(catalogPort.getProduct(second)).thenReturn(snapshot(second, "EUR", true));

        assertThatThrownBy(() -> service.create(new CreateOrderRequest(
                List.of(new OrderItemRequest(productId, 1), new OrderItemRequest(second, 1)))))
                .isInstanceOf(MixedCurrenciesNotAllowedException.class);
    }
}
