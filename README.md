# Healthcare Data Integration Service

## Overview

This project is a reference implementation of a hospital-grade data integration service built with Java 21 and Spring Boot 3. It focuses on securely ingesting heterogeneous clinical data sources, normalizing them to HL7® FHIR® resources, and exposing aggregated patient data via REST APIs for downstream consumers (care teams, analytics, research portals).

## Target Capabilities

- **Source ingestion**: Pluggable connectors for HL7 v2 feeds, CSV batch uploads, and vendor-specific REST APIs.
- **Normalization pipeline**: Message validation, vocabulary mapping (LOINC, SNOMED), transformation to internal canonical models, and FHIR resource projection (Patient, Encounter, Observation, Medication).
- **Master patient index (MPI)**: Deterministic and probabilistic matching to unify patient identities across systems.
- **Consent-aware data services**: Apply facility policies and per-patient consent flags before exposing data.
- **Developer APIs**: REST endpoints for patient snapshot, encounter timeline, observation queries, and ingestion status.
- **Operational guardrails**: Audit logging, observability (OpenTelemetry), circuit breakers, and message replay/failover.

## High-Level Architecture

| Layer            | Responsibilities                                                             | Technologies                                               |
| ---------------- | ---------------------------------------------------------------------------- | ---------------------------------------------------------- |
| Edge APIs        | REST endpoints, request validation, OAuth2 client credentials, rate limiting | Spring MVC, Spring Security, Spring Cloud Gateway (future) |
| Application Core | Command handlers, aggregation services, MPI, consent rules                   | Spring Boot, MapStruct, custom domain modules              |
| Data Pipeline    | Async ingestion, normalization, retries, dead-letter queues                  | Spring Cloud Stream, Apache Kafka, Debezium (optional)     |
| Persistence      | Operational DB, schema migrations, caching                                   | PostgreSQL, Flyway, Spring Data JPA, Redis                 |
| Observability    | Tracing, metrics, audit trail                                                | OpenTelemetry, Micrometer, ELK/OTEL collectors             |

## Initial Scope (MVP)

1. **Patient snapshot API**: `GET /api/v1/patients/{id}` returns demographics, encounters, and recent vitals.
2. **Observation ingestion API**: `POST /api/v1/ingest/observations` accepts batched vitals payload, validates, stores raw message, and publishes normalization task.
3. **Normalization worker**: Consumes Kafka topic, maps observation payloads to FHIR `Observation` + `Patient` references, persists records.
4. **Audit & metrics**: Basic request logging, structured events, Prometheus metrics.

## Data Model (MVP)

- `Patient`: core identity, MRN, demographics, consent flags.
- `Encounter`: visit metadata linked to patient.
- `Observation`: clinical measurements (lab, vitals) linked to patient & encounter.
- `RawMessage`: raw ingest payload + status.

Each entity will have created/updated timestamps, tenant/facility identifiers, and soft-delete flags for compliance.

## Security & Compliance

- OAuth2 client-credential flow for machine-to-machine integrations (Keycloak/Okta compatible).
- Field-level encryption for PHI-at-rest (PostgreSQL pgcrypto) and TLS 1.3 in transit.
- Audit log (FHIR AuditEvent) persisted to append-only store.
- Configurable data retention & deletion jobs.

## Roadmap

1. Scaffold Spring Boot service with modular packages (`api`, `application`, `domain`, `infrastructure`).
2. Configure PostgreSQL schema via Flyway; add Docker Compose with Postgres + Kafka + Redpanda console.
3. Implement ingestion REST controller, DTO validation, service orchestration, and persistence.
4. Add Kafka producer/consumer pipeline for async normalization.
5. Implement patient snapshot query with caching.
6. Harden with integration tests (Testcontainers), security tests, and documentation.

## Getting Started

### Prerequisites

- Java 21, Maven 3.9+
- Docker (for Postgres + Redpanda via `docker-compose.yml`)

### Bootstrap the platform

```bash
docker compose up -d
./mvnw clean verify
SPRING_PROFILES_ACTIVE=dev APP_API_KEY=local-dev-key ./mvnw spring-boot:run
```

### Security

All API calls (except `/actuator/**` and Swagger docs) require the header `X-API-Key`. Default key: `local-dev-key` (override via `APP_API_KEY` env var or `app.auth.api-key` config).

### REST APIs (MVP)

| Method | Path                           | Description                                                                                 |
| ------ | ------------------------------ | ------------------------------------------------------------------------------------------- |
| POST   | `/api/v1/ingest/observations`  | Ingest batched observation payload, persists patient context, publishes normalization event |
| GET    | `/api/v1/patients/{patientId}` | Returns patient demographics, last 10 encounters, last 20 observations                      |

OpenAPI UI available at `/swagger-ui.html`.

### Messaging

Observation ingestion emits `ObservationNormalizationEvent` through Spring Cloud Stream binding `observationNormalization-out-0`. The provided Redpanda broker exposes topic `observation-normalization`.

---

> _FHIR® is a registered trademark of HL7 and is used with the permission of HL7._
