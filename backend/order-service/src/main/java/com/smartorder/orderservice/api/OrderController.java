package com.smartorder.orderservice.api;

import com.smartorder.orderservice.application.dto.OrderDto;
import com.smartorder.orderservice.application.service.OrderCommandService;
import com.smartorder.orderservice.application.service.OrderQueryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@Validated
public class OrderController {

    private final OrderCommandService commandService;
    private final OrderQueryService queryService;

    public OrderController(OrderCommandService commandService, OrderQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody CreateOrderRequest request) {
        UUID id = commandService.create(request);
        return ResponseEntity.created(URI.create("/orders/" + id)).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> get(@PathVariable UUID id) {
        return ResponseEntity.ok(queryService.get(id));
    }

    public record CreateOrderRequest(@NotEmpty List<OrderItemRequest> items) {}

    public record OrderItemRequest(@NotNull UUID productId, int quantity) {}
}

