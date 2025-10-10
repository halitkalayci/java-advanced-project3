package com.smartorder.orderservice.infrastructure.persistence.jdbc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table("orders")
class OrderEntity implements Persistable<UUID> {

    @Id
    @Column("id")
    private UUID id;
    
    @Column("status")
    private String status;
    
    @Column("total_cents")
    private long totalCents;
    
    @Column("currency")
    private String currency;
    
    @Column("created_at")
    private Instant createdAt;

    @MappedCollection(idColumn = "order_id", keyColumn = "id")
    private List<OrderLineEntity> lines = new ArrayList<>();

    @Transient
    private boolean isNew = true;

    OrderEntity() {}

    OrderEntity(UUID id, String status, long totalCents, String currency, Instant createdAt, List<OrderLineEntity> lines) {
        this.id = id;
        this.status = status;
        this.totalCents = totalCents;
        this.currency = currency;
        this.createdAt = createdAt;
        this.lines = new ArrayList<>(lines);
    }

    public UUID getId() {
        return id;
    }

    void setId(UUID id) {
        this.id = id;
    }

    String getStatus() {
        return status;
    }

    void setStatus(String status) {
        this.status = status;
    }

    long getTotalCents() {
        return totalCents;
    }

    void setTotalCents(long totalCents) {
        this.totalCents = totalCents;
    }

    String getCurrency() {
        return currency;
    }

    void setCurrency(String currency) {
        this.currency = currency;
    }

    Instant getCreatedAt() {
        return createdAt;
    }

    void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    List<OrderLineEntity> getLines() {
        return lines;
    }

    void setLines(List<OrderLineEntity> lines) {
        this.lines = new ArrayList<>(lines);
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    void markAsNotNew() {
        this.isNew = false;
    }
}

