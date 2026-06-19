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

```
claude-code-hackathon/
├── spring-music/          # Monolith shell (proxy + frontend + aux endpoints)
├── album-service/         # Extracted Album Catalog microservice
├── user-stories.md        # User stories with acceptance criteria
├── test-all.sh            # Runs both test suites
└── README.md              # This file
```

## If We Had More Time

1. **The Fence (anti-corruption layer)** — Add a test that fails if any monolith domain field name leaks into the album-service API. Add a `PreToolUse` hook preventing Claude from writing cross-boundary code.
2. **The Map (ADR)** — Formal decomposition ADR naming seams, ranking by extraction risk, documenting what we chose not to do.
3. **The Scorecard (eval harness)** — Golden set of correct vs. incorrect seams, eval that scores Claude's boundary proposals.
4. **Replace RestTemplate with WebClient** — non-blocking proxy for better throughput under load.
5. **Resolve D-01 and D-04** — Add delete confirmation (or not) and track count field based on stakeholder resolution.

## How We Used Claude Code

- **Plan mode** for the extraction strategy before writing code — explored the codebase, identified coupling points, and got alignment on approach.
- **Task tracking** to structure the multi-step extraction (5 tasks, executed sequentially with dependencies).
- **CLAUDE.md at two levels** — project-level for both `spring-music/` and `album-service/` so future sessions understand the split architecture immediately.
- **Characterization-first testing** — pinned behavior before the cut, then rewrote tests to verify the proxy contract.
