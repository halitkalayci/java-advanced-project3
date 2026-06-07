package com.smartorder.orderservice.infrastructure.persistence.jdbc;

import java.util.UUID;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

interface OrderJdbcRepository extends CrudRepository<OrderEntity, UUID> {

    /**
     * Optimistic-locked status update. Order lines are immutable after creation,
     * so an existing order only ever changes its status — we update the header
     * row in place (no delete/recreate of lines) and bump the version. Returns
     * the number of rows affected: 0 means a concurrent update changed the
     * version first (optimistic-lock conflict).
     */
    @Modifying
    @Query("UPDATE orders SET status = :status, version = version + 1 "
            + "WHERE id = :id AND version = :expectedVersion")
    int updateStatus(@Param("id") UUID id,
                     @Param("status") String status,
                     @Param("expectedVersion") long expectedVersion);
}
