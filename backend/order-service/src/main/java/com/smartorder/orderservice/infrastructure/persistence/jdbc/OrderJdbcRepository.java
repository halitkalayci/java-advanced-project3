package com.smartorder.orderservice.infrastructure.persistence.jdbc;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

interface OrderJdbcRepository extends CrudRepository<OrderEntity, UUID> {}

