package com.smartorder.catalogservice.api;

import com.smartorder.catalogservice.application.dto.ProductCreateRequest;
import com.smartorder.catalogservice.application.dto.ProductDto;
import com.smartorder.catalogservice.application.dto.ProductUpdateRequest;
import com.smartorder.catalogservice.application.service.ProductCommandService;
import com.smartorder.catalogservice.application.service.ProductQueryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductCommandService commandService;
    private final ProductQueryService queryService;

    public ProductController(ProductCommandService commandService, ProductQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody ProductCreateRequest request) {
        UUID id = commandService.create(request);
        return ResponseEntity.created(URI.create("/products/" + id)).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(
            @PathVariable UUID id, @Valid @RequestBody ProductUpdateRequest request) {
        return ResponseEntity.ok(commandService.update(id, request));
    }

    @GetMapping("{id}")
    public ResponseEntity<ProductDto> get(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(queryService.get(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> list(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        return ResponseEntity.ok(queryService.list(page, size));
    }
}

