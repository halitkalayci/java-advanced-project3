package com.smartorder.orderservice.infrastructure.persistence.jdbc;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("outbox_events")
public class OrderOutboxEntity implements Persistable<UUID> {

    @Id
    @Column("id")
    private UUID id;
    
    @Column("aggregate_type")
    private String aggregateType;
    
    @Column("aggregate_id")
    private UUID aggregateId;
    
    @Column("type")
    private String type;
    
    @Column("payload")
    private String payload;
    
    @Column("created_at")
    private Instant createdAt;
    
    @Column("published_at")
    private Instant publishedAt;
    
    @Transient
    private boolean isNew = true;

    public OrderOutboxEntity() {}

    public OrderOutboxEntity(
            UUID id,
            String aggregateType,
            UUID aggregateId,
            String type,
            String payload,
            Instant createdAt,
            Instant publishedAt) {
        this.id = id;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.type = type;
        this.payload = payload;
        this.createdAt = createdAt;
        this.publishedAt = publishedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public String getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }
    
    @Override
    public boolean isNew() {
        return isNew;
    }
    
    public void markAsNotNew() {
        this.isNew = false;
    }
}

