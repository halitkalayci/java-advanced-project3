# 0002 — Idempotency, Outbox & resilience

## Status
Accepted

## Context
Payment used a check-then-act idempotency that could double-charge under concurrency. The outbox
publisher had no concurrency control (duplicate events with multiple instances), and the catalog
client had no timeouts/circuit breaker (thread-hang risk).

## Decision
- **Payment idempotency**: an atomic `claim()` (`INSERT … ON CONFLICT DO NOTHING`) — only the writer
  that inserts the PENDING row processes the payment.
- **Outbox**: retry counter + dead-letter threshold; **ShedLock** so only one instance publishes.
- **Order updates**: optimistic locking + immutable order lines (targeted status UPDATE).
- **Catalog client**: Resilience4j timeouts + circuit breaker (404 ignored) + time limiter;
  transient failures surface as HTTP 503.
- **Gateway**: per-route circuit breaker with a 503 fallback + a simple rate limiter.

## Consequences
- Correct-once payment processing; no duplicate domain events across instances.
- The rate limiter is in-memory (per instance) — a shared store (Redis) is the scale-out path.
