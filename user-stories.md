# Spring Music — User Stories

## Business Capabilities

Spring Music has two core business capabilities: **browsing** the music catalog and **managing** catalog entries.

---

## Capability 1: Browse the Music Catalog

### US-01 — View the catalog
As a music fan, I want to see all albums in the catalog so I can discover what's available.

**Acceptance criteria:**
- Given the catalog has albums, when I open the app, then I see all albums displayed with title, artist, release year, and genre
- Given the catalog is empty, when I open the app, then I see a clear empty state (not a blank page or error)
- Given more than 10 albums exist, when I view the catalog, then all are shown without pagination or truncation

---

### US-02 — Switch display layout
As a music fan, I want to toggle between grid and list views so I can browse in the format I prefer.

**Acceptance criteria:**
- Given I am in grid view, when I click the list toggle, then albums reformat into a list without a page reload
- Given I switch views, when I navigate away and return, then my view preference is not retained (session behavior, not persistence)

---

## Capability 2: Manage Catalog Entries

### US-03 — Add an album
As a catalog manager, I want to add a new album so the catalog reflects our full inventory.

**Acceptance criteria:**
- Given I open the add form, when I leave Title, Artist, Release Year, or Genre blank, then the OK button is disabled and I cannot submit
- Given I enter a release year outside the format `1000–2999`, when I attempt to submit, then the field is flagged invalid and submission is blocked
- Given I submit a valid album, when the save completes, then the album appears in the catalog immediately without a full page reload
- Given the save fails (server error), when the response returns, then an error message with the HTTP status code is shown

---

### US-04 — Edit an album
As a catalog manager, I want to edit an existing album so I can correct mistakes.

**Acceptance criteria:**
- Given I open the edit form, when it appears, then all current field values are pre-populated
- Given I click Cancel, when the modal closes, then no changes are persisted
- Given I submit a valid edit, when the save completes, then the updated values appear in the catalog immediately
- Given I click a field directly in the list, when the inline editor appears, then I can edit and save that field without opening the modal

---

### US-05 — Delete an album
As a catalog manager, I want to remove an album so retired entries don't clutter the catalog.

**Acceptance criteria:**
- Given I delete an album, when the action completes, then the album is permanently removed and no longer appears in the catalog
- Given the delete fails, when the response returns, then an error message with the HTTP status code is shown
- **Open (D-01):** Is immediate hard-delete acceptable, or does this action require a confirmation step before executing?

---

## Stakeholder Disagreements

These are unresolved priority conflicts that must be decided before writing tests or cutting service seams.

| # | Disagreement | Position A | Position B | Why it blocks |
|---|---|---|---|---|
| **D-01** | Confirm before delete? | **PM:** No confirm — catalog managers know what they're doing | **Ops/QA:** Deletes are unrecoverable, a confirmation dialog is required | Changes US-05 acceptance criteria and the DELETE contract test |
| **D-02** | Is multi-backend support a real feature or a CF demo artifact to drop? | **Architect:** Drop it — it is the most complex code in the app and exists only for Cloud Foundry demos | **PM:** Keep it — it is the differentiator for the platform story | Biggest modernization decision; dropping it removes `SpringApplicationContextInitializer` and simplifies all extraction work |
| **D-03** | Who is the catalog manager — same person as the end user, or a distinct role requiring authentication? | **PM:** Internal tool, no auth needed, everyone can edit | **Security:** Any public-facing catalog needs role separation | No auth exists today; adding it changes scope and blocks US-03–05 from being tested against a real role boundary |
| **D-04** | Should track count be a manageable field? | **PM:** Yes — it is on every physical album, omitting it is a gap | **Dev:** It is in the data model but absent from the form; adding it is scope creep for now | Affects acceptance criteria for US-03 and US-04 directly |

> **D-02 and D-03 are blocking.** Resolve these before writing the decomposition ADR or scaffolding contract tests.
