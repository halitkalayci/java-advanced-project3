# SmartOrder — Enterprise Hardening (v2)

This document records the changes that took SmartOrder from an educational prototype
to a production-shaped reference project. It is the "what changed and why" companion to
the main [README](../README.md) and the [ADRs](./adr).

> Verification note: configuration is served by **Config Server in `git` mode**, so
> changes under `backend/configs/**` only take effect after they are **committed & pushed**
> (or when Config Server is run with a local/native profile).

---

## Phase 0 — Foundation
- **Flyway migrations** replace `schema.sql` for catalog/order/payment
  (`db/migration/V1__init.sql`, portable across H2 and PostgreSQL; `CLOB`→`TEXT`).
- **Database strategy**: H2 (file) by default in `dev`; **PostgreSQL + HikariCP** under the
  `prod` profile. Service configs split into `base` + `-dev` + `-prod`.
- **`.env.example`** added; real `.env` is git-ignored. H2 console no longer allows remote access.
- Kafka host-dev broker default unified to the **EXTERNAL listener `localhost:9094`**.

## Phase 1 — Security (P0)
- **OAuth2 enforced everywhere**: catalog/order/payment/notification + Gateway switched from
  `permitAll()` to `authenticated()`. Keycloak realm roles → `ROLE_*` via a JWT converter.
- **order→catalog token relay** (Feign interceptor) so internal calls carry the bearer token.
- **Secrets externalized**: BFF Keycloak secret → `${KEYCLOAK_BFF_SECRET}`; issuers standardized
  to the `smartorder-bff` realm. Token-leaking `DEBUG` logs removed.
- **BFF proxy hardened**: path-traversal guard, header forwarding, response propagation; the
  service-discovery debug endpoint moved behind authentication. Security headers + CORS whitelist.
- **Config Server & Eureka** protected with HTTP Basic auth (env-default credentials so dev still
  works out of the box; override in prod).

## Phase 2 — Architecture, Resilience & Data
- **Hexagonal fix**: `CatalogPort` + `ProductSnapshot` (application port); Feign moved to an adapter,
  so the use-case no longer imports infrastructure. Dead `catalogTimeoutMillis` removed.
- **Resilience4j** on the catalog Feign client: connect/read timeouts, circuit breaker (404 ignored),
  time limiter. Transient failures map to **503**; proper RFC-ish problem responses for 404/422/503/400.
- **Gateway**: per-route circuit breaker + `/fallback` (503) + in-memory rate limiter (429).
- **Payment idempotency race fixed**: atomic `claim()` (`INSERT … ON CONFLICT DO NOTHING`) replaces the
  check-then-act that could double-charge.
- **Order lines immutable**: status updates use an optimistic-locked targeted UPDATE (no more
  delete/recreate that regenerated line IDs). `orders.version` column added.
- **Outbox resilience**: retry counter + dead-letter threshold; **ShedLock** ensures a single publisher
  across instances (no duplicate Kafka events).

## Phase 3 — Messaging & Tests
- **Notifications best-effort**: email failures are logged, not re-thrown (no retry-storm duplicate
  emails). Blocking `Thread.sleep` removed; logs are single-line/structured.
- **Test suite from zero** (22 tests, all run without Docker): unit (Mockito) for the order/payment
  use-cases and domain, `@WebMvcTest` for controller security (401/201/400), `@DataJdbcTest` for
  persistence (optimistic locking, outbox). **JaCoCo** wired in.
- These tests caught a real bug: the exception handler was returning **200** for every error.

## Phase 4 — Observability, Containerization & CI/CD
- **Observability**: Micrometer Tracing (Brave) → Zipkin, Prometheus metrics (`/actuator/prometheus`),
  and JSON (ECS) logs under `prod`. Cross-cutting config centralized in `configs/application.yml`.
- **Docker**: one **generic `backend/Dockerfile`** (build-arg `MODULE`) builds all 8 services;
  multi-stage frontend image with nginx (SPA fallback, security headers, BFF proxy).
- **`docker-compose.yml`** now runs the whole stack (8 services + frontend + Zipkin + infra) with
  healthchecks and ordered startup.
- **Kubernetes**: namespace, Secret, ConfigMap, Deployments+Services for every service, Ingress, HPA.
- **CI/CD**: GitHub Actions (build+test+JaCoCo, frontend build, Trivy scan; image publish to GHCR) and
  Dependabot (maven/npm/actions/docker).

## Phase 5 — Frontend & Docs
- Global error handling via a **toast** service + HTTP interceptor; loading/empty/error states;
  subscription leak fixes (`takeUntilDestroyed`).
- **Auth state cached** (short TTL) so route changes don't hammer the BFF; **logout clears the cart**.
- **Environment split**: dev talks to the BFF directly; prod uses same-origin relative URLs (nginx proxy).
- This document + ADRs.

---

## Known runtime caveats (verify when running the full stack)
- **Keycloak issuer hostname**: in docker-compose, add `127.0.0.1 keycloak` to your hosts file so the
  browser and the services use the same issuer host.
- **`KEYCLOAK_BFF_SECRET`** must be set (rotate the value that previously lived in git history).
- **H2 `ON CONFLICT DO NOTHING`** (payment claim): verify on first run; falls back to a duplicate-key
  catch if unsupported.
- **K8s** assumes external Keycloak/Kafka/Zipkin, an nginx ingress controller, and metrics-server;
  the `prod` profile expects PostgreSQL.

## Deliberately left as "advanced extensions" (documented, not implemented)
HashiCorp Vault / Sealed Secrets, service mesh / mTLS, Helm charts, Spring Cloud Contract,
full NetworkPolicy/RBAC/PDB set, Grafana dashboards, notification-DB consumer idempotency,
DLT consumer, Testcontainers integration tests.
