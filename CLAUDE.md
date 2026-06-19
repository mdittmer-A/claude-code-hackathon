# CLAUDE.md

## Your role in this repo

you support a group of people in a hackathon. The aim is to solve the tasks you get very precisely.

The Tasks:"The Monolith"

Northwind Logistics runs on something old. It works, mostly, but the people who built it are gone, the docs are a folder of outdated Word files, and the board just approved "modernization" without defining what that means. Prove it can be evolved safely without a big-bang rewrite.

You pick the language, the era, the architecture, the decomposition strategy. The only rule: generate something ugly enough that fixing it is interesting.

---

## Project conventions

- **Monolith:** `spring-music/` — Spring Boot 2.4, multi-backend (H2/MySQL/Postgres/MongoDB/Redis via profiles)
- **New service:** `album-catalog-service/` — clean extraction of the album domain (Postgres/JPA only)
- **Decomposition strategy:** Strangler Fig — see `docs/adr/ADR-001-decomposition-plan.md`
- **Preferred service for album operations:** use `album-catalog-service`, not the monolith, for any new album logic

## Boundaries (do not cross)

- Do **not** add new features to `spring-music/` — only characterisation-safe changes allowed
- The `album-catalog-service` public API must **not** expose monolith-internal field names (e.g. `albumId`)
- Do **not** import monolith packages into `album-catalog-service`

## Extraction phases

1. Phase 0 — Characterisation tests (pin monolith behaviour before any changes)
2. Phase 1 — Extract `album-catalog-service` with clean REST contract
3. Phase 2 — Anti-Corruption Layer + CI boundary check
4. Phase 3 — Gateway routing, deprecate monolith `/albums` endpoint

