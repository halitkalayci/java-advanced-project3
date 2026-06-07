# 0003 — Observability stack

## Status
Accepted

## Context
Only basic actuator health was available — no tracing, metrics, or machine-readable logs.

## Decision
- **Distributed tracing** with Micrometer Tracing (Brave) exporting to **Zipkin**.
- **Metrics** via Micrometer + Prometheus (`/actuator/prometheus`).
- **Structured JSON (ECS) logs** under the `prod` profile.
- Cross-cutting observability config lives in a single `configs/application.yml` served by Config
  Server to every application.

## Consequences
- End-to-end request correlation across gateway → services.
- Zipkin must be reachable (added to docker-compose); sampling is tunable via `SO_TRACING_SAMPLING`.
