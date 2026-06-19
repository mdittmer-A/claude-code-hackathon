# Spring Music — Verification Checklist

> Mark each item PASS / FAIL / N/A when testing a modernized version.  
> Base URL: `http://localhost:8080` (adjust as needed)

---

## API — Album CRUD

| # | Test | Pass | Fail | Notes |
|---|------|:----:|:----:|-------|
| 1.1 | GET `/albums` returns JSON array with 29 albums | [ ] | [ ] | |
| 1.2 | GET `/albums/{id}` returns single album JSON | [ ] | [ ] | |
| 1.3 | GET `/albums/{bad-id}` returns null/empty (not 404) | [ ] | [ ] | |
| 1.4 | PUT `/albums` with valid body creates album with generated ID | [ ] | [ ] | |
| 1.5 | POST `/albums` with existing ID updates the album | [ ] | [ ] | |
| 1.6 | DELETE `/albums/{id}` removes album | [ ] | [ ] | |
| 1.7 | DELETE `/albums/{bad-id}` does not crash | [ ] | [ ] | |
| 1.8 | PUT `/albums` with empty body returns 400 | [ ] | [ ] | |

## API — Info & Services

| # | Test | Pass | Fail | Notes |
|---|------|:----:|:----:|-------|
| 2.1 | GET `/appinfo` returns `{profiles, services}` | [ ] | [ ] | |
| 2.2 | GET `/service` returns array (empty locally) | [ ] | [ ] | |

## API — Error Endpoints

| # | Test | Pass | Fail | Notes |
|---|------|:----:|:----:|-------|
| 3.1 | GET `/errors/kill` kills the app | [ ] | [ ] | |
| 3.2 | GET `/errors/throw` returns 500 | [ ] | [ ] | |
| 3.3 | GET `/errors/fill-heap` causes OOM crash | [ ] | [ ] | |

## API — Actuator

| # | Test | Pass | Fail | Notes |
|---|------|:----:|:----:|-------|
| 4.1 | GET `/actuator/health` returns UP with details | [ ] | [ ] | |
| 4.2 | GET `/actuator` lists all endpoints | [ ] | [ ] | |

## Seed Data

| # | Test | Pass | Fail | Notes |
|---|------|:----:|:----:|-------|
| 5.1 | Fresh start: 29 albums loaded | [ ] | [ ] | |
| 5.2 | Restart with data: no duplicates added | [ ] | [ ] | |
| 5.3 | Spot-check: Nirvana "Nevermind" 1991 Rock present | [ ] | [ ] | |
| 5.4 | Spot-check: Michael Jackson "Thriller" 1982 Pop present | [ ] | [ ] | |
| 5.5 | Spot-check: BB King "Singin' The Blues" 1956 Blues present | [ ] | [ ] | |
| 5.6 | Genre counts: Rock=22, Pop=1, Blues=6 | [ ] | [ ] | |
| 5.7 | Album IDs are strings, max 40 chars | [ ] | [ ] | |

## UI — Views & Sorting

| # | Test | Pass | Fail | Notes |
|---|------|:----:|:----:|-------|
| 6.1 | Default view is grid (card tiles) | [ ] | [ ] | |
| 6.2 | Click list icon -> table view | [ ] | [ ] | |
| 6.3 | Click grid icon -> grid view | [ ] | [ ] | |
| 6.4 | Sort by title works | [ ] | [ ] | |
| 6.5 | Sort by artist works | [ ] | [ ] | |
| 6.6 | Sort by year works | [ ] | [ ] | |
| 6.7 | Sort by genre works | [ ] | [ ] | |
| 6.8 | Toggle sort direction (asc/desc) | [ ] | [ ] | |

## UI — Album CRUD

| # | Test | Pass | Fail | Notes |
|---|------|:----:|:----:|-------|
| 7.1 | "Add an album" opens modal form | [ ] | [ ] | |
| 7.2 | Form requires all 4 fields | [ ] | [ ] | |
| 7.3 | Release Year rejects non-year input | [ ] | [ ] | |
| 7.4 | OK disabled until form valid | [ ] | [ ] | |
| 7.5 | Submit adds album to list | [ ] | [ ] | |
| 7.6 | Cancel closes modal, no change | [ ] | [ ] | |
| 7.7 | Gear -> edit opens pre-filled modal | [ ] | [ ] | |
| 7.8 | Inline edit: click field opens editor | [ ] | [ ] | |
| 7.9 | Inline edit: Enter saves | [ ] | [ ] | |
| 7.10 | Inline edit: Esc cancels | [ ] | [ ] | |
| 7.11 | Inline edit: empty value rejected | [ ] | [ ] | |
| 7.12 | Gear -> delete removes album | [ ] | [ ] | |

## UI — Status Messages

| # | Test | Pass | Fail | Notes |
|---|------|:----:|:----:|-------|
| 8.1 | Success: green alert on save/delete | [ ] | [ ] | |
| 8.2 | Error: red alert on failure | [ ] | [ ] | |
| 8.3 | Dismiss: X button clears alert | [ ] | [ ] | |

## UI — Navigation

| # | Test | Pass | Fail | Notes |
|---|------|:----:|:----:|-------|
| 9.1 | Header shows "Spring Music" with icon | [ ] | [ ] | |
| 9.2 | Info dropdown shows profiles & services | [ ] | [ ] | |
| 9.3 | `#/errors` route shows error buttons | [ ] | [ ] | |
| 9.4 | Unknown route defaults to albums view | [ ] | [ ] | |

## Database Profiles

| # | Test | Pass | Fail | Notes |
|---|------|:----:|:----:|-------|
| 10.1 | No profile -> H2 in-memory works | [ ] | [ ] | |
| 10.2 | `mysql` profile -> MySQL connection | [ ] | [ ] | |
| 10.3 | `postgres` profile -> Postgres connection | [ ] | [ ] | |
| 10.4 | `mongodb` profile -> MongoDB works | [ ] | [ ] | |
| 10.5 | `redis` profile -> Redis works | [ ] | [ ] | |
| 10.6 | Multiple profiles -> startup fails with error | [ ] | [ ] | |
| 10.7 | Active profile visible in `/appinfo` | [ ] | [ ] | |

## Non-Functional

| # | Test | Pass | Fail | Notes |
|---|------|:----:|:----:|-------|
| 11.1 | App starts on port 8080 | [ ] | [ ] | |
| 11.2 | Static files served (index.html, CSS, JS) | [ ] | [ ] | |
| 11.3 | API returns application/json | [ ] | [ ] | |
| 11.4 | No manual DB schema setup needed | [ ] | [ ] | |

---

**Tested by:** ___________________  
**Date:** ___________________  
**Version under test:** ___________________  
**Total:** ___ Pass / ___ Fail / ___ N/A
