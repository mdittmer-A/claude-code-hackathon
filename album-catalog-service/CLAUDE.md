# CLAUDE.md — album-catalog-service (New Service)

This is the **extracted album catalog service** — the first seam cut from the spring-music monolith.

## Rules for this directory

- Public API must **not** expose monolith-internal field names (`albumId`, `trackCount` as int, etc.)
- Do **not** import any class from `org.cloudfoundry.samples.music.*`
- Persistence: **PostgreSQL + JPA only** — no Redis/MongoDB profiles
- No `CfEnv` dependency — use standard Spring Boot environment abstraction instead

## API contract

- `GET    /albums`        — list all albums
- `GET    /albums/{id}`   — get single album
- `PUT    /albums`        — create album (returns created entity)
- `POST   /albums/{id}`   — update album
- `DELETE /albums/{id}`   — delete album

Response shape must match the monolith's output exactly during the transition window (Phases 1–3) so the Angular SPA requires no changes.

## Test strategy

- Contract tests (`AlbumContractTest`) must pass on every commit alongside the monolith characterisation suite
- Any change to the public `Album` response shape requires updating the contract test first
