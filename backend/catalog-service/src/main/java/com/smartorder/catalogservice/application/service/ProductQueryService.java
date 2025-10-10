package com.smartorder.catalogservice.application.service;

import com.smartorder.catalogservice.application.dto.ProductDto;
import com.smartorder.catalogservice.domain.port.ProductRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ProductQueryService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductQueryService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public ProductDto get(UUID id) {
        return productRepository
                .findById(id)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public List<ProductDto> list(int page, int size) {
        int pageSafe = Math.max(page, 0);
        int sizeSafe = size <= 0 ? 20 : Math.min(size, 100);
        int offset = pageSafe * sizeSafe;
        return productRepository.findAll(offset, sizeSafe).stream().map(productMapper::toDto).toList();
    }
}

