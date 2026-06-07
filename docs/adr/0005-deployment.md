# 0005 — Containerization & deployment

## Status
Accepted

## Context
Only 2 of 8 services had Dockerfiles, docker-compose ran infra only, K8s covered 2 services, and
there was no CI/CD.

## Decision
- **One generic `backend/Dockerfile`** parameterized by a `MODULE` build-arg builds every service
  (multi-stage, non-root, layered jar) — no per-service duplication.
- **Full docker-compose** runs the entire stack (8 services + frontend + Zipkin + infra) with
  healthchecks and ordered startup.
- **Kubernetes** manifests for every service plus namespace, Secret, ConfigMap, Ingress, HPA.
- **CI/CD** with GitHub Actions (build/test/coverage, frontend build, Trivy scan, GHCR image publish)
  and Dependabot.

## Consequences
- The whole platform is reproducible with one `docker compose up`.
- K8s assumes external Keycloak/Kafka/Zipkin, an ingress controller, and metrics-server.
