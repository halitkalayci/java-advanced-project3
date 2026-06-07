package com.smartorder.orderservice.application.port;

import java.util.UUID;

/**
 * Outbound port for reading catalog products. The application depends on this
 * abstraction; the infrastructure layer adapts the concrete transport (Feign)
 * to it, so the use-case stays free of framework/transport details.
 *
 * <p>Implementations translate transport errors into application exceptions:
 * a missing product into {@code CatalogProductNotFoundException} and any
 * transient/unknown failure into {@code CatalogUnavailableException}.
 */
public interface CatalogPort {

    ProductSnapshot getProduct(UUID productId);
}
