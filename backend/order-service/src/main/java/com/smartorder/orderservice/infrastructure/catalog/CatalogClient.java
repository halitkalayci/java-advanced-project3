package com.smartorder.orderservice.infrastructure.catalog;

import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service")
public interface CatalogClient {

    @GetMapping("/products/{id}")
    CatalogProductResponse getProduct(@PathVariable("id") UUID id);
}

