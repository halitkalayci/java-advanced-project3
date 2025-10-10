package com.smartorder.catalogservice.domain.port;

import com.smartorder.catalogservice.domain.model.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    UUID save(Product product);

    void update(Product product);

    Optional<Product> findById(UUID id);

    List<Product> findAll(int offset, int limit);
}

