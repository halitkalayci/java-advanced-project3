package com.smartorder.orderservice.infrastructure.persistence.jdbc;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

interface OrderLineJdbcRepository extends CrudRepository<OrderLineEntity, UUID> {

    List<OrderLineEntity> findByOrderId(UUID orderId);
}

