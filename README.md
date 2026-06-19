# Claude Code Hackathon — Code Modernization

## Scenario

Scenario 1: **Code Modernization** — "The Monolith"

Using the [Spring Music](https://github.com/rishikeshradhakrishnan/spring-music) BYO monolith option.

## What We Built

A strangler-fig service extraction from the Spring Music monolith. The Album Catalog capability (CRUD operations on albums — user stories US-01 through US-05) has been extracted into a standalone microservice (`album-service/`), while the monolith (`spring-music/`) continues to serve the AngularJS frontend by proxying album requests to the new service.

Both projects compile, run, and pass their test suites independently on the same commit. The monolith's characterization tests use WireMock to simulate the downstream service, so no orchestration is needed for `./gradlew test`. The album-service's contract tests verify the exact API shape the monolith relies on.

## Challenges Attempted

| # | Challenge | Status | Notes |
|---|---|---|---|
| 1 | The Stories (PM) | done | User stories with acceptance criteria in `user-stories.md`. Stakeholder disagreements D-01 through D-04 captured explicitly. |
| 4 | The Pin (Tester) | done | Characterization tests pin monolith behavior before extraction (WireMock-based after proxy cut). |
| 5 | The Cut (Dev) | done | Album service extracted with clean API contract. Both suites green on same commit. |

## Key Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| D-02: Drop multi-backend? | **Drop it** | CF-only demo complexity (`SpringApplicationContextInitializer`, MongoDB, Redis) was the riskiest code and blocked extraction. Removing it simplified everything downstream. |
| D-03: Auth needed? | **No auth** | Internal tool, no auth today. Adding it is scope creep for the extraction proof. |
| Spring Boot version | **3.2.5** (up from 2.4.0) | Required by JDK 21 (only available JDK on the build machine). Also migrates `javax.*` → `jakarta.*`. |
| Test isolation | **WireMock** for monolith tests | Makes `./gradlew test` self-contained. Contract tests in album-service prove the real service honors the contract. |
| Proxy approach | **RestTemplate** in monolith | Simplest option for synchronous pass-through. No service mesh, no discovery — just a URL property. |

## How to Run It

```bash
# Prerequisites: JDK 21
brew install openjdk@21
export JAVA_HOME=/opt/homebrew/Cellar/openjdk@21/21.0.11/libexec/openjdk.jdk/Contents/Home

# Run all tests (both projects, no external dependencies)
cd album-service && ./gradlew test && cd ../spring-music && ./gradlew test

# Run the full stack
cd album-service && ./gradlew bootRun &   # starts on port 8081
cd spring-music && ./gradlew bootRun &     # starts on port 8080
open http://localhost:8080                  # AngularJS frontend, same as before
```

## Project Structure

## Our Work — Scenario 1: Code Modernization

### Participants
- (fill in names and roles)

### What We Built

A **Strangler Fig decomposition** of the Spring Music monolith into a clean `album-catalog-service`, with full characterization testing to guarantee behavioral equivalence.

**What exists in this repo:**

| Directory | What it is |
|-----------|-----------|
| `spring-music/` | The legacy monolith (unchanged) |
| `album-catalog-service/` | Extracted album service — Postgres/JPA only, clean REST |
| `docs/adr/` | ADR-001: Decomposition Plan |
| `tests/` | 87 characterization + contract test cases (Jest + Playwright) |
| `user-stories.md` | User stories with acceptance criteria and stakeholder disagreements |

### Challenges Attempted

| # | Challenge | Status | Notes |
|---|-----------|--------|-------|
| 1 | The Stories (PM) | Done | `user-stories.md` — 5 user stories, 4 open disagreements |
| 2 | The Patient (Architect) | Done | Using BYO monolith (spring-music) |
| 3 | The Map (Architect) | Done | `docs/adr/ADR-001-decomposition-plan.md` |
| 4 | The Pin (Tester) | In progress | `tests/` — 87 test cases, automated API + UI suite |
| 5 | The Cut (Dev) | Not started | `album-catalog-service/` has contract only |
| 6 | The Fence (Dev/Tester) | Not started | ACL rules defined in CLAUDE.md |

### Key Decisions

1. **Strangler Fig over big-bang rewrite** — incremental extraction with gateway routing
2. **Postgres-only for new service** — drops multi-backend complexity (Redis/Mongo/H2 profiles)
3. **Characterization tests before any code change** — behavior-pinning, bugs included
4. **Anti-corruption boundary** — `albumId` and `CfEnv` must never leak into new service
5. **Three-level CLAUDE.md** — project, monolith, and service each get their own context

See `docs/adr/ADR-001-decomposition-plan.md` for full rationale.

### How to Run It

```bash
# Run the monolith
cd spring-music
./gradlew clean assemble
java -jar build/libs/spring-music-1.0.jar
# App at http://localhost:8080

# Run characterization tests (requires monolith running)
cd tests
npm install
npx playwright install   # one-time browser setup
npx jest                 # API tests
npx playwright test      # UI tests
```

### If We Had More Time

1. Implement `album-catalog-service` (Phase 1 — The Cut)
2. Add API gateway routing with rollback capability
3. Automate ACL boundary checks in CI (grep for banned field names)
4. Extract frontend SPA to static hosting
5. Add contract tests that run both services side-by-side

### How We Used Claude Code

- **Tester role:** Claude explored the full monolith codebase and produced a complete behavior catalog (87 test cases) mapped to user stories
- **Three-level CLAUDE.md:** Taught Claude the project boundaries so it respects extraction rules
- **ADR generation:** Claude produced the decomposition plan with risk ranking and "what we chose NOT to do"
- **Test automation:** Generated Jest + Playwright test suite from the specification

