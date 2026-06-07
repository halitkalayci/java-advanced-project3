# 0004 — Testing strategy

## Status
Accepted

## Context
The codebase had zero tests.

## Decision
Adopt a test pyramid that runs entirely in the build (no Docker required for the default suite):
- **Unit** (Mockito) for use-cases and domain invariants.
- **`@WebMvcTest`** for controller + security behavior (401/201/400).
- **`@DataJdbcTest`** against H2 (PostgreSQL mode) for persistence — optimistic locking, outbox.
- **JaCoCo** for coverage; CI runs `mvn verify`.

Testcontainers-based integration tests are documented as an extension (require Docker).

## Consequences
- Regressions are caught in CI; the suite already caught a real bug (all errors returned HTTP 200).
- `@DataJdbcTest` uses the same H2 dialect/identifier casing as dev to match Flyway-created tables.
