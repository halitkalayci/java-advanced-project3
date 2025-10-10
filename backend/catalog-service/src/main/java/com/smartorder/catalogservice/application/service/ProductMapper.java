package com.smartorder.catalogservice.application.service;

import com.smartorder.catalogservice.application.dto.ProductDto;
import com.smartorder.catalogservice.domain.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    default ProductDto toDto(Product product) {
        return new ProductDto(
                product.id(),
                product.name(),
                product.unitCents(),
                product.currency(),
                product.active(),
                product.createdAt(),
                product.updatedAt());
    }
}

