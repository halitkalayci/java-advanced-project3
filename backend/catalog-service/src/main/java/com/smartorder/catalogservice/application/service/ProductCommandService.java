package com.smartorder.catalogservice.application.service;

import com.smartorder.catalogservice.application.dto.ProductCreateRequest;
import com.smartorder.catalogservice.application.dto.ProductDto;
import com.smartorder.catalogservice.application.dto.ProductUpdateRequest;
import com.smartorder.catalogservice.domain.model.Product;
import com.smartorder.catalogservice.domain.port.ProductRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ProductCommandService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final Clock clock;

    public ProductCommandService(ProductRepository productRepository, ProductMapper productMapper, Clock clock) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.clock = clock;
    }

    public UUID create(ProductCreateRequest request) {
        Instant now = Instant.now(clock);
        Product product = new Product(
                UUID.randomUUID(),
                request.name(),
                request.unitCents(),
                request.currency(),
                request.active(),
                now,
                now);
        productRepository.save(product);
        return product.id();
    }

    public ProductDto update(UUID id, ProductUpdateRequest request) {
        Product existing = productRepository
                .findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        Product updated = existing.update(
                request.unitCents(),
                request.currency(),
                request.active(),
                Instant.now(clock));
        productRepository.update(updated);
        return productMapper.toDto(updated);
    }
}

