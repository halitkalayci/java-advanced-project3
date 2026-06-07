package com.smartorder.orderservice.infrastructure.persistence.jdbc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface OrderOutboxRepository extends CrudRepository<OrderOutboxEntity, UUID> {

    @Query("SELECT * FROM outbox_events "
            + "WHERE published_at IS NULL AND retry_count < :maxRetries "
            + "ORDER BY created_at LIMIT :limit")
    List<OrderOutboxEntity> findUnpublished(@Param("maxRetries") int maxRetries, @Param("limit") int limit);

    @Modifying
    @Query("UPDATE outbox_events SET published_at = :publishedAt WHERE id = :id")
    void markPublished(@Param("id") UUID id, @Param("publishedAt") Instant publishedAt);

    @Modifying
    @Query("UPDATE outbox_events SET retry_count = retry_count + 1, last_error = :error WHERE id = :id")
    void recordFailure(@Param("id") UUID id, @Param("error") String error);
}
