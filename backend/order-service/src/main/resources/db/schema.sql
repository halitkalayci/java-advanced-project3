CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    total_cents BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS order_lines (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    unit_cents BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL,
    quantity INT NOT NULL,
    line_total_cents BIGINT NOT NULL,
    CONSTRAINT fk_order_lines_order
        FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE TABLE IF NOT EXISTS outbox_events (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(64) NOT NULL,
    aggregate_id UUID NOT NULL,
    type VARCHAR(128) NOT NULL,
    payload CLOB NOT NULL,
    created_at TIMESTAMP NOT NULL,
    published_at TIMESTAMP
);

