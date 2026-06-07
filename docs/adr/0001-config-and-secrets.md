# 0001 — Centralized config & secret management

## Status
Accepted

## Context
Configuration was duplicated and a Keycloak client secret was committed in plain text.
Config Server and Eureka were open to anyone.

## Decision
- Keep **Config Server (git backend)** as the single source of config; split each service into
  `base + dev + prod` so common settings live once and only the datasource differs per environment.
- Move all secrets to **environment variables** (`.env`, git-ignored; `.env.example` as the template).
- Protect **Config Server and Eureka with HTTP Basic auth**, using env-var credentials whose dev
  defaults match on both sides so local startup needs no extra setup; production overrides them.

## Consequences
- Config changes require commit & push (git backend) to take effect.
- The previously committed secret must be rotated in Keycloak.
- Production still needs a real secret store (Vault / Sealed Secrets) — noted as an extension.
