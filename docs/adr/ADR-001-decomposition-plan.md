# ADR-001: Decomposition Plan — Spring Music Monolith

**Status:** Proposed  
**Date:** 2026-06-19  
**Deciders:** Northwind Logistics Architecture Team

---

## Context

Spring Music is a Spring Boot 2.4 monolith originally built as a Cloud Foundry sample application. It ships business logic, persistence abstraction, and a static Angular frontend as a single deployable WAR/JAR. The app supports six different persistence backends (H2, MySQL, PostgreSQL, MSSQL, MongoDB, Redis) via runtime Spring profiles and `CfEnv` service-binding detection.

### Current Architecture

```
spring-music/
├── web/            AlbumController, InfoController, ErrorController
├── domain/         Album, ApplicationInfo, RandomIdGenerator
├── repositories/   AlbumRepositoryPopulator
│   ├── jpa/        JpaAlbumRepository
│   ├── mongodb/    MongoAlbumRepository
│   └── redis/      RedisAlbumRepository  (hand-rolled CrudRepository impl)
├── config/         SpringApplicationContextInitializer (profile + DB wiring)
└── resources/
    ├── albums.json (static seed data)
    └── static/     Angular 1.x SPA (Bootstrap 3)
```

**Key observations:**

| Problem | Location | Severity |
|---|---|---|
| DB wiring and profile logic coupled inside app bootstrap | `SpringApplicationContextInitializer` | High |
| `AlbumController` injects raw `CrudRepository<Album,String>` — any backend change leaks into the controller | `AlbumController.java:14` | High |
| `RedisAlbumRepository` hand-implements `CrudRepository` instead of using Spring Data Redis | `redis/RedisAlbumRepository.java` | Medium |
| `AlbumRepositoryPopulator` uses `BeanFactoryUtils` to find *any* `CrudRepository` at runtime — fragile | `AlbumRepositoryPopulator.java:21` | Medium |
| `InfoController` directly instantiates `CfEnv` — Cloud Foundry lock-in | `InfoController.java:18` | Medium |
| Angular 1.x SPA served from the same JAR | `resources/static/` | Low |
| `albumId` field on `Album` is orphaned (never set by any write path) | `Album.java:24` | Low |
| No service layer — controller → repository with no business-logic tier | global | High |

---

## Decision

We adopt a **Strangler Fig** pattern with an **API Façade** as the initial extraction target. The monolith continues to serve all traffic. A new `album-catalog` service is extracted first because it:

1. Has a clean, stable API surface (`/albums` CRUD)
2. Owns a single domain entity (`Album`)
3. Has zero inbound dependencies from other services
4. Carries no coupling to the Cloud Foundry binding machinery

A thin HTTP façade (reverse proxy or gateway rule) routes `/albums/**` to the new service while all other paths stay on the monolith.

---

## Named Seams

### Seam 1 — Album Catalog (extraction target)
- **Boundary:** Everything behind `/albums` — CRUD operations on `Album` objects
- **What moves:** `Album`, `AlbumController`, all three repository implementations, `AlbumRepositoryPopulator`, seed data `albums.json`
- **New service name:** `album-catalog-service`
- **API contract:** REST JSON, same URL shape as today (`GET/PUT/POST/DELETE /albums/{id}`)
- **Data store choice:** PostgreSQL (JPA path) — removes the multi-backend complexity from day one; Redis/Mongo profiles are dropped

### Seam 2 — Environment / Platform Info (defer)
- **Boundary:** `/appinfo` and `/service` in `InfoController`
- **Problem:** Tight `CfEnv` coupling; not business logic
- **Decision:** Wrap in an environment-abstraction interface first; extract only after containerisation removes CF dependency

### Seam 3 — Frontend SPA (defer)
- **Boundary:** `resources/static/` — Angular 1.x, Bootstrap 3
- **Decision:** Serve as a standalone static site (nginx / CDN) after Seam 1 is stable; not worth extracting before the API surface is locked

---

## Extraction Risk Ranking

Lower rank = extract first.

| Rank | Seam | Coupling | Test Coverage | Data-Model Tangle | Business Criticality | Notes |
|---|---|---|---|---|---|---|
| 1 | Album Catalog | Low — single domain object, no FK deps | Minimal characterisation tests needed | None — `Album` has no FK to other entities | High — core feature | **Extract first** |
| 2 | Platform Info | Low — reads env only | None | None | Low | Block on containerisation |
| 3 | Frontend SPA | Zero backend coupling | N/A | None | Medium | Block on API stability |

---

## Migration Strategy: Strangler Fig Steps

```
Phase 0 — Pin (The Pin, ADR-002)
  Write characterisation tests against the monolith for /albums behaviour.
  These tests must pass before and after every subsequent phase.

Phase 1 — Extract album-catalog-service
  New Spring Boot service, PostgreSQL only, clean REST API.
  Monolith still deployed. API gateway routes /albums/** to new service.
  Contract test added: CharacterisationSuite + AlbumContractTest green on same commit.

Phase 2 — Anti-Corruption Layer (The Fence)
  album-catalog-service public API must not expose monolith field names (albumId, etc).
  CI check: grep for banned field names in OpenAPI spec output.

Phase 3 — Deprecate monolith /albums endpoint
  Remove AlbumController from monolith. Monolith becomes shell for Phase 2+ seams.

Phase 4 — Frontend SPA extraction
  Serve static/ from CDN. Monolith serves no frontend.

Phase 5 — Platform Info extraction
  Replace CfEnv with Kubernetes environment abstraction. Extract InfoController.

Phase 6 — Monolith retirement
  No remaining traffic. Decommission.
```

---

## What We Chose NOT to Do

| Option | Why rejected |
|---|---|
| **Big-bang rewrite** | No regression safety net; team has no full understanding of edge cases in the existing behaviour |
| **Microservices-first decomposition** (split all seams at once) | Too much risk surface in parallel; the characterisation suite cannot protect you if every seam is in flight simultaneously |
| **Keep multi-backend persistence** | Adds operational complexity and prevents clean contract definition; PostgreSQL covers 95% of production use |
| **Event-driven / Kafka backbone from day one** | Album Catalog is a synchronous CRUD service; introducing async messaging before the boundary is stable is premature |
| **Lift-and-shift containerisation first** | Containerising the monolith without extracting seams defers the hard work and bakes in the existing coupling |
| **GraphQL façade** | REST is already the client contract; adding a translation layer increases blast radius for no customer benefit at this stage |

---

## Three-Level CLAUDE.md Strategy

Per ADR recommendation, three `CLAUDE.md` files govern Claude's assistance:

| Level | File | Purpose |
|---|---|---|
| User | `~/.claude/CLAUDE.md` | Personal preferences (verbosity, language, code style) |
| Project | `/CLAUDE.md` | Codebase conventions, hackathon context, do-not-cross boundaries |
| Monolith root | `/spring-music/CLAUDE.md` | Legacy-specific notes: do not add new features; only characterisation-safe changes |
| New service root | `/album-catalog-service/CLAUDE.md` | No monolith field names in public API; prefer new service for album operations |

---

## Consequences

**Positive:**
- Characterisation suite provides a regression net before any code moves
- Strangler Fig means zero downtime cutover; rollback is a gateway rule change
- Each phase is independently deployable and testable

**Negative:**
- Dual-write / dual-deploy period increases operational overhead for ~2 sprints
- API gateway becomes a new infrastructure dependency
- Team must maintain two services during the transition window

**Risks:**
- `albumId` field semantics are unknown — must be investigated before Phase 2 (may be an external reference)
- Seed-data loading via `AlbumRepositoryPopulator` must be replicated exactly or characterisation tests will diverge on empty-DB scenarios
