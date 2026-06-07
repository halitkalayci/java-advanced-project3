-- Optimistic locking for orders, outbox retry tracking, and ShedLock table.
-- Portable across H2 (MODE=PostgreSQL) and PostgreSQL.

ALTER TABLE orders ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE outbox_events ADD COLUMN retry_count INT NOT NULL DEFAULT 0;
ALTER TABLE outbox_events ADD COLUMN last_error VARCHAR(1000);

-- Distributed scheduler lock (ShedLock) so only one instance publishes the outbox.
CREATE TABLE IF NOT EXISTS shedlock (
    name VARCHAR(64) NOT NULL,
    lock_until TIMESTAMP NOT NULL,
    locked_at TIMESTAMP NOT NULL,
    locked_by VARCHAR(255) NOT NULL,
    PRIMARY KEY (name)
);
