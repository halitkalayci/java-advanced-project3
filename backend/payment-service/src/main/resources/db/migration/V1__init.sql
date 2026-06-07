-- Payment service baseline schema.
-- Portable across H2 (MODE=PostgreSQL) and PostgreSQL.

CREATE TABLE IF NOT EXISTS payment_requests (
    order_id UUID PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    last_attempt_at TIMESTAMP
);
