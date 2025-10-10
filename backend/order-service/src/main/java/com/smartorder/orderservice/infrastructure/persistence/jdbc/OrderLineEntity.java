package com.smartorder.orderservice.infrastructure.persistence.jdbc;

import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("order_lines")
class OrderLineEntity implements Persistable<UUID> {

    @Id
    @Column("id")
    private UUID id;
    
    @Column("order_id")
    private UUID orderId;
    
    @Column("product_id")
    private UUID productId;
    
    @Column("product_name")
    private String productName;
    
    @Column("unit_cents")
    private long unitCents;
    
    @Column("currency")
    private String currency;
    
    @Column("quantity")
    private int quantity;
    
    @Column("line_total_cents")
    private long lineTotalCents;

    @Transient
    private boolean isNew = true;

    OrderLineEntity() {}

    OrderLineEntity(
            UUID id,
            UUID orderId,
            UUID productId,
            String productName,
            long unitCents,
            String currency,
            int quantity,
            long lineTotalCents) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.unitCents = unitCents;
        this.currency = currency;
        this.quantity = quantity;
        this.lineTotalCents = lineTotalCents;
    }

    public UUID getId() {
        return id;
    }

    void setId(UUID id) {
        this.id = id;
    }

    UUID getOrderId() {
        return orderId;
    }

    void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    UUID getProductId() {
        return productId;
    }

    void setProductId(UUID productId) {
        this.productId = productId;
    }

    String getProductName() {
        return productName;
    }

    void setProductName(String productName) {
        this.productName = productName;
    }

    long getUnitCents() {
        return unitCents;
    }

    void setUnitCents(long unitCents) {
        this.unitCents = unitCents;
    }

    String getCurrency() {
        return currency;
    }

    void setCurrency(String currency) {
        this.currency = currency;
    }

    int getQuantity() {
        return quantity;
    }

    void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    long getLineTotalCents() {
        return lineTotalCents;
    }

    void setLineTotalCents(long lineTotalCents) {
        this.lineTotalCents = lineTotalCents;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    void markAsNotNew() {
        this.isNew = false;
    }
}

