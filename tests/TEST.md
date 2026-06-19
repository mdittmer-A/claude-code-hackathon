# Spring Music — Test Cases (mapped to User Stories)

> Each test traces back to a user story (US-xx) or to a system behavior not covered by stories.  
> Architecture decision: **Strangler Fig** — new `album-catalog-service` (Postgres/JPA) extracts `/albums` CRUD.  
> Tests cover both the **monolith** (characterization) and the **new service** (contract).

## Readiness Overview

| Section | Phase | Ready? | Blocker |
|---------|-------|--------|---------|
| US-01 – US-05 (UI tests) | 0 — Pin | **YES** | Run monolith locally |
| T-SYS.1 – T-SYS.26 (system) | 0 — Pin | **YES** | Run monolith locally |
| T-CT.1 – T-CT.8 (contract) | 1 — Extract | **NO** | `album-catalog-service` not yet implemented |
| T-ACL.1 – T-ACL.4 (anti-corruption) | 2 — ACL | **NO** | Needs service + OpenAPI spec |
| T-RT.1 – T-RT.3 (routing) | 3 — Route | **NO** | Needs API gateway setup |

### How to Run (Phase 0 — now)

```bash
# 1. Build and start the monolith
cd spring-music
./gradlew clean assemble
java -jar build/libs/spring-music-1.0.jar
# App runs at http://localhost:8080

# 2. Run automated tests (from tests/ directory)
cd tests
npm install
npx jest                    # API tests (runs without browser)
npx playwright test         # UI tests (launches browser)
```

### Test Stack

- **API tests:** Jest + `node-fetch` — black-box HTTP calls against `/albums`, `/appinfo`, `/actuator`
- **UI tests:** Playwright — browser automation for grid/list view, modals, inline editing
- **Both run against:** `http://localhost:8080` (configurable via `BASE_URL` env var)

### Resolved Decisions

| ID | Resolution | Impact |
|----|-----------|--------|
| D-02 | Multi-backend **dropped** — new service is Postgres-only | Monolith DB profile tests become legacy-only (N/A for new service) |
| D-03 | No auth for now (internal tool) | No auth tests needed yet |

### Still Open

| ID | Issue | Tests Affected |
|----|-------|---------------|
| D-01 | Confirm before delete? | T-05.1 (may need confirmation step) |
| D-04 | trackCount in form? | T-03.1, T-03.2 (form field list) |

---

## US-01 — View the Catalog

| # | Test Case | Steps | Expected Result | Status |
|---|-----------|-------|-----------------|--------|
| T-01.1 | All albums visible on load | Open app at `/` | 29 albums displayed with title, artist, year, genre | [ ] |
| T-01.2 | Album fields shown correctly | Check any album card/row | Shows: title, artist, releaseYear, genre | [ ] |
| T-01.3 | No pagination or truncation | Count visible albums | All 29 visible without scrolling through pages | [ ] |
| T-01.4 | Empty catalog state | Start app with empty DB, delete all albums | No error, graceful empty state (not blank page) | [ ] |
| T-01.5 | Sort by title | Click "title" sort link | Albums reorder alphabetically by title | [ ] |
| T-01.6 | Sort by artist | Click "artist" sort link | Albums reorder alphabetically by artist | [ ] |
| T-01.7 | Sort by year | Click "year" sort link | Albums reorder by releaseYear | [ ] |
| T-01.8 | Sort by genre | Click "genre" sort link | Albums reorder alphabetically by genre | [ ] |
| T-01.9 | Toggle sort direction | Click chevron icon | Order reverses (asc↔desc), icon changes | [ ] |

---

## US-02 — Switch Display Layout

| # | Test Case | Steps | Expected Result | Status |
|---|-----------|-------|-----------------|--------|
| T-02.1 | Default is grid view | Open app fresh | Albums shown as card tiles | [ ] |
| T-02.2 | Switch to list view | Click list icon (th-list) | Albums shown in table: Title, Artist, Year, Genre columns | [ ] |
| T-02.3 | Switch back to grid | Click grid icon (th) | Albums shown as card tiles again | [ ] |
| T-02.4 | No page reload on switch | Observe browser during toggle | No full page reload, smooth transition | [ ] |
| T-02.5 | View not persisted | Switch to list, refresh page | Reverts to grid view (default) | [ ] |

---

## US-03 — Add an Album

| # | Test Case | Steps | Expected Result | Status |
|---|-----------|-------|-----------------|--------|
| T-03.1 | Open add form | Click "+ add an album" | Modal opens with empty fields: Title, Artist, Release Year, Genre | [ ] |
| T-03.2 | All fields required | Leave any field blank | OK button disabled, warning icon on empty field | [ ] |
| T-03.3 | Title required | Fill all except Title | OK disabled, Title shows warning | [ ] |
| T-03.4 | Artist required | Fill all except Artist | OK disabled, Artist shows warning | [ ] |
| T-03.5 | Release Year required | Fill all except Year | OK disabled, Year shows warning | [ ] |
| T-03.6 | Genre required | Fill all except Genre | OK disabled, Genre shows warning | [ ] |
| T-03.7 | Year validation — valid | Enter "2020" | Field shows green checkmark, OK enabled (if others valid) | [ ] |
| T-03.8 | Year validation — invalid text | Enter "abc" | Field flagged invalid, OK disabled | [ ] |
| T-03.9 | Year validation — too short | Enter "20" | Field flagged invalid, OK disabled | [ ] |
| T-03.10 | Year validation — out of range | Enter "0999" | Field flagged invalid (pattern: `^[1-2]\d{3}$`) | [ ] |
| T-03.11 | Year validation — boundary | Enter "1000" | Field valid (matches pattern) | [ ] |
| T-03.12 | Submit valid album | Fill all fields valid, click OK | Modal closes, album appears in catalog, success message shown | [ ] |
| T-03.13 | No page reload on add | Observe browser after submit | Album appears without full page reload | [ ] |
| T-03.14 | Cancel add | Click Cancel | Modal closes, no album added, no status message | [ ] |
| T-03.15 | Server error on add | Simulate server error (e.g. stop DB) | Red error message: "Error saving album: {status}" | [ ] |
| T-03.16 | Generated ID | After adding, GET `/albums` | New album has server-generated string ID (max 40 chars) | [ ] |

> **D-04 (open):** `trackCount` is in the data model but not in the add form. If resolved as "add it", tests T-03.1 and T-03.2 need updating.

---

## US-04 — Edit an Album

| # | Test Case | Steps | Expected Result | Status |
|---|-----------|-------|-----------------|--------|
| T-04.1 | Open edit modal | Gear icon → "edit" | Modal opens with all fields pre-filled with current values | [ ] |
| T-04.2 | Fields pre-populated | Check each field in edit modal | Title, Artist, Year, Genre match the album's current values | [ ] |
| T-04.3 | Edit and save via modal | Change title, click OK | Modal closes, updated value appears in catalog | [ ] |
| T-04.4 | Cancel edit modal | Click Cancel | Modal closes, no changes persisted | [ ] |
| T-04.5 | Inline edit — activate | Click directly on field value (grid or list view) | Inline editor appears with input + OK/Cancel buttons | [ ] |
| T-04.6 | Inline edit — save via Enter | Type new value, press Enter | Field updates, success message shown | [ ] |
| T-04.7 | Inline edit — save via button | Type new value, click checkmark | Field updates, success message shown | [ ] |
| T-04.8 | Inline edit — cancel via Esc | Press Esc | Reverts to original value, no save | [ ] |
| T-04.9 | Inline edit — cancel via button | Click X button | Reverts to original value, no save | [ ] |
| T-04.10 | Inline edit — empty rejected | Clear value, try to save | Save rejected (returns false), original value preserved | [ ] |
| T-04.11 | Edit via API | POST `/albums` with modified album JSON | Updated album returned, change visible in catalog | [ ] |

---

## US-05 — Delete an Album

| # | Test Case | Steps | Expected Result | Status |
|---|-----------|-------|-----------------|--------|
| T-05.1 | Delete via UI | Gear icon → "delete" | Album removed from catalog, success message: "Album deleted" | [ ] |
| T-05.2 | Permanent removal | After delete, refresh page | Album does not reappear | [ ] |
| T-05.3 | Delete via API | DELETE `/albums/{id}` | HTTP 200, album gone from GET `/albums` | [ ] |
| T-05.4 | Delete non-existent | DELETE `/albums/{bad-id}` | No crash, graceful handling | [ ] |
| T-05.5 | Error on delete failure | Simulate server error during delete | Red error message: "Error deleting album: {status}" | [ ] |

> **D-01 (open):** Currently NO confirmation dialog before delete. If resolved as "require confirmation", add test T-05.6 for confirmation step.

---

## System Behavior (not in user stories)

### Info & Monitoring

| # | Test Case | Steps | Expected Result | Status |
|---|-----------|-------|-----------------|--------|
| T-SYS.1 | App info endpoint | GET `/appinfo` | Returns `{"profiles": [...], "services": [...]}` | [ ] |
| T-SYS.2 | Service info endpoint | GET `/service` | Returns array (empty when local) | [ ] |
| T-SYS.3 | Info dropdown in UI | Click info icon in navbar | Shows active profiles and services | [ ] |
| T-SYS.4 | Health endpoint | GET `/actuator/health` | Returns `{"status":"UP"}` with details | [ ] |
| T-SYS.5 | Actuator endpoints | GET `/actuator` | Lists all actuator endpoints | [ ] |

### Error Simulation

| # | Test Case | Steps | Expected Result | Status |
|---|-----------|-------|-----------------|--------|
| T-SYS.6 | Kill endpoint | GET `/errors/kill` | App exits, connection drops | [ ] |
| T-SYS.7 | Throw endpoint | GET `/errors/throw` | HTTP 500 (NullPointerException) | [ ] |
| T-SYS.8 | Fill-heap endpoint | GET `/errors/fill-heap` | OOM crash | [ ] |
| T-SYS.9 | Errors page in UI | Navigate to `#/errors` | Shows Kill and Throw Exception buttons | [ ] |

### Seed Data

| # | Test Case | Steps | Expected Result | Status |
|---|-----------|-------|-----------------|--------|
| T-SYS.10 | Seed data on fresh start | Start with empty DB | 29 albums loaded | [ ] |
| T-SYS.11 | No re-seeding | Restart with existing data | No duplicates, count unchanged | [ ] |
| T-SYS.12 | Seed data content | Check against SEED_DATA_REFERENCE.md | All 29 albums match exactly | [ ] |

### Database Profiles (Monolith — legacy only)

| # | Test Case | Steps | Expected Result | Applies To |
|---|-----------|-------|-----------------|------------|
| T-SYS.13 | Default (no profile) | Start without profile | H2 in-memory DB, app works | Monolith only |
| T-SYS.14 | MySQL profile | Start with `-Dspring.profiles.active=mysql` | Connects to MySQL | Monolith only (N/A for new service) |
| T-SYS.15 | Postgres profile | Start with `-Dspring.profiles.active=postgres` | Connects to PostgreSQL | Monolith + new service |
| T-SYS.16 | MongoDB profile | Start with `-Dspring.profiles.active=mongodb` | Uses MongoDB | Monolith only (N/A for new service) |
| T-SYS.17 | Redis profile | Start with `-Dspring.profiles.active=redis` | Uses Redis | Monolith only (N/A for new service) |
| T-SYS.18 | Multiple profiles fail | Start with two DB profiles | App fails with IllegalStateException | Monolith only (N/A for new service) |
| T-SYS.19 | Profile in appinfo | GET `/appinfo` | Active profile name in `profiles` array | Monolith only |

> **D-02 resolved:** Multi-backend dropped in new service. Tests T-SYS.14/16/17/18 are N/A for `album-catalog-service`.

### Navigation & Static Assets

| # | Test Case | Steps | Expected Result | Status |
|---|-----------|-------|-----------------|--------|
| T-SYS.20 | App starts on port 8080 | Start app, hit `http://localhost:8080` | Index page loads | [ ] |
| T-SYS.21 | Static files served | Request CSS/JS/images | 200 OK with correct MIME types | [ ] |
| T-SYS.22 | Default route | Navigate to `#/unknown` | Shows albums view (not 404) | [ ] |
| T-SYS.23 | Header branding | Check navbar | "Spring Music" with music icon | [ ] |

### Status Messages

| # | Test Case | Steps | Expected Result | Status |
|---|-----------|-------|-----------------|--------|
| T-SYS.24 | Success message format | Save an album | Green alert: "Album saved" | [ ] |
| T-SYS.25 | Error message format | Force a save failure | Red alert: "Error saving album: {status}" | [ ] |
| T-SYS.26 | Dismiss message | Click X on alert | Alert disappears | [ ] |

---

## Contract Tests — `album-catalog-service` (New Service)

> These tests verify the new service behaves identically to the monolith for the `/albums` API.  
> Per ADR-001: response shape must match monolith during transition (Phases 1–3).

### API Contract — Equivalence

| # | Test Case | Method | URL | Expected (must match monolith) | Status |
|---|-----------|--------|-----|-------------------------------|--------|
| T-CT.1 | List all albums | GET | `/albums` | JSON array, same structure as monolith | [ ] |
| T-CT.2 | Get album by ID | GET | `/albums/{id}` | JSON object with: id, title, artist, releaseYear, genre, trackCount | [ ] |
| T-CT.3 | Get non-existent album | GET | `/albums/{id}` | Same behavior as monolith (null/empty) | [ ] |
| T-CT.4 | Create album | PUT | `/albums` | Returns album with generated ID, same JSON shape | [ ] |
| T-CT.5 | Update album | POST | `/albums/{id}` | Returns updated album (note: URL includes `{id}` in new service) | [ ] |
| T-CT.6 | Delete album | DELETE | `/albums/{id}` | HTTP 200, album removed | [ ] |
| T-CT.7 | Validation error | PUT | `/albums` with empty body | HTTP 400 | [ ] |
| T-CT.8 | Seed data loaded | Fresh start | 29 albums present, same content as monolith | [ ] |

### Anti-Corruption Layer (Phase 2)

| # | Test Case | Steps | Expected Result | Status |
|---|-----------|-------|-----------------|--------|
| T-ACL.1 | No `albumId` in response | GET `/albums`, inspect all fields | `albumId` field must NOT appear in response JSON | [ ] |
| T-ACL.2 | No monolith internals leaked | Inspect OpenAPI/response schema | No fields from monolith that are not in public contract | [ ] |
| T-ACL.3 | No monolith package imports | Grep `album-catalog-service` source | Zero imports of `org.cloudfoundry.samples.music.*` | [ ] |
| T-ACL.4 | No CfEnv dependency | Check build file | No `java-cfenv` dependency | [ ] |

### Response Shape Comparison

| Field | Monolith | New Service | Rule |
|-------|----------|-------------|------|
| `id` | String (random, max 40) | String (random, max 40) | Must match |
| `title` | String | String | Must match |
| `artist` | String | String | Must match |
| `releaseYear` | String | String | Must match |
| `genre` | String | String | Must match |
| `trackCount` | int (default 0) | int (default 0) | Must match |
| `albumId` | String (always null) | **Must NOT appear** | Anti-corruption rule |

### Transition Routing (Phase 3)

| # | Test Case | Steps | Expected Result | Status |
|---|-----------|-------|-----------------|--------|
| T-RT.1 | Gateway routes /albums to new service | Call `/albums` through gateway | Response from album-catalog-service | [ ] |
| T-RT.2 | Other paths stay on monolith | Call `/appinfo` through gateway | Response from monolith | [ ] |
| T-RT.3 | Rollback: route back to monolith | Switch gateway rule | `/albums` served by monolith again, no data loss | [ ] |

---

## Coverage Matrix

| Area | Test Cases | Count |
|------|-----------|-------|
| US-01 View catalog | T-01.1 – T-01.9 | 9 |
| US-02 Switch layout | T-02.1 – T-02.5 | 5 |
| US-03 Add album | T-03.1 – T-03.16 | 16 |
| US-04 Edit album | T-04.1 – T-04.11 | 11 |
| US-05 Delete album | T-05.1 – T-05.5 | 5 |
| System behavior | T-SYS.1 – T-SYS.26 | 26 |
| Contract tests (new service) | T-CT.1 – T-CT.8 | 8 |
| Anti-corruption layer | T-ACL.1 – T-ACL.4 | 4 |
| Transition routing | T-RT.1 – T-RT.3 | 3 |
| **Total** | | **87** |

---

## Test Execution Strategy by Phase

| Phase | What to run | Pass criteria |
|-------|------------|---------------|
| Phase 0 (Pin) | T-01 through T-SYS — all against monolith | All pass (baseline) |
| Phase 1 (Extract) | T-CT.1–CT.8 against new service + full monolith suite still green | Both pass on same commit |
| Phase 2 (ACL) | T-ACL.1–ACL.4 against new service | All pass — no monolith internals leak |
| Phase 3 (Route) | T-RT.1–RT.3 + full suite through gateway | All pass — gateway routes correctly, rollback works |

---

---

## Appendix A: Seed Data Reference (all 29 albums)

| # | Artist | Title | Year | Genre |
|---|--------|-------|------|-------|
| 1 | Nirvana | Nevermind | 1991 | Rock |
| 2 | The Beach Boys | Pet Sounds | 1966 | Rock |
| 3 | Marvin Gaye | What's Going On | 1971 | Rock |
| 4 | Jimi Hendrix Experience | Are You Experienced? | 1967 | Rock |
| 5 | U2 | The Joshua Tree | 1987 | Rock |
| 6 | The Beatles | Abbey Road | 1969 | Rock |
| 7 | Fleetwood Mac | Rumours | 1977 | Rock |
| 8 | Elvis Presley | Sun Sessions | 1976 | Rock |
| 9 | Michael Jackson | Thriller | 1982 | Pop |
| 10 | The Rolling Stones | Exile on Main Street | 1972 | Rock |
| 11 | Bruce Springsteen | Born to Run | 1975 | Rock |
| 12 | The Clash | London Calling | 1980 | Rock |
| 13 | The Eagles | Hotel California | 1976 | Rock |
| 14 | Led Zeppelin | Led Zeppelin | 1969 | Rock |
| 15 | Led Zeppelin | IV | 1971 | Rock |
| 16 | Police | Synchronicity | 1983 | Rock |
| 17 | U2 | Achtung Baby | 1991 | Rock |
| 18 | The Rolling Stones | Let it Bleed | 1969 | Rock |
| 19 | The Beatles | Rubber Soul | 1965 | Rock |
| 20 | The Ramones | The Ramones | 1976 | Rock |
| 21 | Queen | A Night At The Opera | 1975 | Rock |
| 22 | Boston | Don't Look Back | 1978 | Rock |
| 23 | BB King | Singin' The Blues | 1956 | Blues |
| 24 | Albert King | Born Under A Bad Sign | 1967 | Blues |
| 25 | Muddy Waters | Folk Singer | 1964 | Blues |
| 26 | The Fabulous Thunderbirds | Rock With Me | 1979 | Blues |
| 27 | Robert Johnson | King of the Delta Blues | 1961 | Blues |
| 28 | Stevie Ray Vaughan | Texas Flood | 1983 | Blues |
| 29 | Stevie Ray Vaughan | Couldn't Stand The Weather | 1984 | Blues |

**Genre counts:** Rock = 22, Pop = 1, Blues = 6  
**Runtime defaults:** `id` = random string (max 40 chars), `trackCount` = 0, `albumId` = null  
**Loading rule:** Only populates if DB is empty (`count() == 0`); no re-seeding on restart.

---

**Tested by:** ___________________  
**Date:** ___________________  
**Version:** ___________________  
**Result:** ___ / 87 Pass | ___ Fail | ___ N/A
