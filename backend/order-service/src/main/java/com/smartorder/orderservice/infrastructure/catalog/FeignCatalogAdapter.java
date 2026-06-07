package com.smartorder.orderservice.infrastructure.catalog;

import com.smartorder.orderservice.application.exception.CatalogProductNotFoundException;
import com.smartorder.orderservice.application.exception.CatalogUnavailableException;
import com.smartorder.orderservice.application.port.CatalogPort;
import com.smartorder.orderservice.application.port.ProductSnapshot;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Adapts the Feign {@link CatalogClient} to the application {@link CatalogPort},
 * translating transport errors into application exceptions so the use-case never
 * sees Feign/HTTP types.
 */
@Component
class FeignCatalogAdapter implements CatalogPort {

    private final CatalogClient catalogClient;

    FeignCatalogAdapter(CatalogClient catalogClient) {
        this.catalogClient = catalogClient;
    }

    @Override
    public ProductSnapshot getProduct(UUID productId) {
        try {
            CatalogProductResponse product = catalogClient.getProduct(productId);
            return new ProductSnapshot(
                    product.id(), product.name(), product.unitCents(), product.currency(), product.active());
        } catch (CatalogNotFoundException e) {
            throw new CatalogProductNotFoundException(productId);
        } catch (RuntimeException e) {
            // Timeout, open circuit, 5xx, deserialization, etc. → treat as transient.
            throw new CatalogUnavailableException(productId, e);
        }
    }
}
