# CLAUDE.md — Northwind Logistics / Spring Music Modernisation

## Project context

Spring Music is a Spring Boot 2.4 monolith being decomposed via **Strangler Fig**.
Full rationale and risk ranking: `docs/adr/ADR-001-decomposition-plan.md`.

Goal: prove the monolith can be evolved safely **without a big-bang rewrite**.

## Codebase structure

| Directory | Role |
|---|---|
| `spring-music/` | Legacy monolith — Spring Boot 2.4, multi-backend (H2/MySQL/Postgres/MongoDB/Redis via Spring profiles) |
| `album-catalog-service/` | Extracted album domain — Postgres/JPA only, clean REST contract |
| `docs/adr/` | Architecture Decision Records |

## Extraction phases

| Phase | Name | Status |
|---|---|---|
| 0 | **The Pin** — characterisation tests that pin monolith behaviour (bugs included) | ✅ Done — `tests/TEST.md` (87 cases), automated in `tests/api/` and `tests/ui/` |
| 1 | **The Cut** — extract `album-catalog-service` with clean REST contract | ⏳ |
| 2 | **The Fence** — Anti-Corruption Layer + CI check that fails on banned field names | ⏳ |
| 3 | Gateway routing — deprecate monolith `/albums` endpoint | ⏳ |

## Boundaries (hard rules — never cross)

- Do **not** add new features to `spring-music/` — only characterisation-safe changes
- The `album-catalog-service` public API must **not** expose monolith-internal field names (`albumId`, `trackCount` as int)
- Do **not** import any class from `org.cloudfoundry.samples.music.*` into `album-catalog-service`

## Preferences (soft rules — follow unless there is a documented reason not to)

- For any new album logic, prefer `album-catalog-service` over the monolith
- Before changing any behaviour in `spring-music/`, there must be a characterisation test that captures the existing behaviour first
- A failing characterisation test after a change = unintended behaviour change → revert first, investigate second

## Test suite

| Location | Stack | What it covers |
|----------|-------|---------------|
| `tests/api/` | Jest + fetch | Album CRUD API, system endpoints, actuator, seed data |
| `tests/ui/` | Playwright | UI views, sorting, add/edit/delete modals, inline editing, navigation |
| `tests/TEST.md` | — | Full spec: 87 test cases mapped to user stories + contract tests |

**Run:** `cd tests && npm install && npx jest && npx playwright test` (requires monolith running on :8080)

**Rule:** Any change to `spring-music/` must not break the characterisation tests. Run them before and after.

## Three-level CLAUDE.md

| Level | File | Purpose |
|---|---|---|
| Project | `/CLAUDE.md` ← this file | Codebase conventions, seam boundaries, extraction phase status |
| Monolith | `/spring-music/CLAUDE.md` | Legacy rules: no new features, known issues, test strategy |
| New service | `/album-catalog-service/CLAUDE.md` | No monolith field names in public API; API contract |

