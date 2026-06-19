# Spring Music — Test Specification

> Behavior-pinning reference for the Spring Music monolith.  
> Use this to verify any modernized version preserves all current functionality.

---

## 1. REST API — Album CRUD (`/albums`)

### 1.1 List all albums
- **Method:** GET
- **URL:** `/albums`
- **Expected:** JSON array containing exactly 29 albums on fresh start
- **Response fields per album:** `id`, `title`, `artist`, `releaseYear`, `genre`, `trackCount`, `albumId`

### 1.2 Get album by ID
- **Method:** GET
- **URL:** `/albums/{id}`
- **Input:** A valid album ID (string, max 40 chars)
- **Expected:** JSON object with all album fields

### 1.3 Get non-existent album
- **Method:** GET
- **URL:** `/albums/{id}`
- **Input:** An ID that does not exist (e.g. `nonexistent-id-123`)
- **Expected:** Returns `null` / empty body (HTTP 200, not 404)

### 1.4 Add new album
- **Method:** PUT
- **URL:** `/albums`
- **Input body:**
  ```json
  {
    "title": "Test Album",
    "artist": "Test Artist",
    "releaseYear": "2020",
    "genre": "Rock"
  }
  ```
- **Expected:** Returns saved album with a server-generated `id` field
- **Verify:** Album now appears in GET `/albums`

### 1.5 Update existing album
- **Method:** POST
- **URL:** `/albums`
- **Input body:** Full album JSON including existing `id`
- **Expected:** Returns updated album with changes persisted

### 1.6 Delete album
- **Method:** DELETE
- **URL:** `/albums/{id}`
- **Input:** Valid existing album ID
- **Expected:** HTTP 200, album no longer returned by GET `/albums`

### 1.7 Delete non-existent album
- **Method:** DELETE
- **URL:** `/albums/{id}`
- **Input:** Non-existent ID
- **Expected:** No crash, graceful handling

### 1.8 Add album — validation failure
- **Method:** PUT
- **URL:** `/albums`
- **Input body:** `{}` (empty object or missing required fields)
- **Expected:** HTTP 400 Bad Request (due to `@Valid` annotation)

---

## 2. REST API — Application Info

### 2.1 Get app info
- **Method:** GET
- **URL:** `/appinfo`
- **Expected:** JSON: `{"profiles": [...], "services": [...]}`
- **Note:** `profiles` contains active Spring profiles, `services` contains CF service names (empty locally)

### 2.2 Get service info
- **Method:** GET
- **URL:** `/service`
- **Expected:** JSON array of Cloud Foundry services (empty array `[]` when running locally)

---

## 3. REST API — Error Endpoints (`/errors`)

### 3.1 Kill application
- **Method:** GET
- **URL:** `/errors/kill`
- **Expected:** Application calls `System.exit(1)` — connection drops, 502 from proxy

### 3.2 Throw exception
- **Method:** GET
- **URL:** `/errors/throw`
- **Expected:** HTTP 500 Internal Server Error (NullPointerException thrown)

### 3.3 Fill heap
- **Method:** GET
- **URL:** `/errors/fill-heap`
- **Expected:** Application crashes with OutOfMemoryError

---

## 4. Spring Boot Actuator

### 4.1 Health endpoint
- **Method:** GET
- **URL:** `/actuator/health`
- **Expected:** `{"status":"UP", ...}` with detail section (show-details: always)

### 4.2 Actuator discovery
- **Method:** GET
- **URL:** `/actuator`
- **Expected:** Lists all available actuator endpoints (all exposed via `include: "*"`)

---

## 5. Seed Data Behavior

### 5.1 Fresh start loads seed data
- **Precondition:** Empty database
- **Expected:** Exactly 29 albums loaded from `albums.json`

### 5.2 Existing data prevents re-seeding
- **Precondition:** Database already has albums (count > 0)
- **Expected:** Seed data is NOT loaded again (no duplicates)

### 5.3 Seed data spot-checks
- Nirvana — "Nevermind" — 1991 — Rock
- Michael Jackson — "Thriller" — 1982 — Pop
- BB King — "Singin' The Blues" — 1956 — Blues
- Led Zeppelin — "IV" — 1971 — Rock

### 5.4 Genre distribution
- Rock: 22 albums
- Pop: 1 album
- Blues: 6 albums

### 5.5 ID generation
- Each album gets a randomly generated string ID
- Maximum length: 40 characters

---

## 6. UI — Album List View

### 6.1 Default view is grid
- On page load, albums display as card tiles (grid layout)

### 6.2 Switch to list view
- Click list icon -> albums display in a table with columns: Title, Artist, Year, Genre

### 6.3 Switch back to grid view
- Click grid icon -> returns to card tile layout

### 6.4 Sort by title
- Click "title" link -> albums ordered alphabetically by title

### 6.5 Sort by artist
- Click "artist" link -> albums ordered alphabetically by artist

### 6.6 Sort by year
- Click "year" link -> albums ordered by releaseYear

### 6.7 Sort by genre
- Click "genre" link -> albums ordered alphabetically by genre

### 6.8 Toggle sort direction
- Click chevron icon -> toggles ascending/descending order
- Visual indicator changes (chevron-up vs chevron-down)

---

## 7. UI — Album CRUD Operations

### 7.1 Add album — open form
- Click "+ add an album" link
- Modal dialog opens with fields: Album Title, Artist, Release Year, Genre

### 7.2 Add album — form validation
- All 4 fields are required (shown with warning icons when invalid)
- Release Year must match pattern `^[1-2]\d{3}$` (e.g. "2020", not "abc")
- Valid fields show green checkmark, invalid show warning icon

### 7.3 Add album — submit
- OK button is disabled until all validations pass
- On submit: modal closes, album appears in list, success message shown

### 7.4 Add album — cancel
- Click Cancel -> modal closes, no album added, no status message

### 7.5 Edit album — via modal
- Click gear icon on album -> click "edit"
- Modal opens pre-filled with existing album data
- Same validation rules as add

### 7.6 Edit album — inline editing
- Click directly on a field value (title, artist, year, genre) in grid or list view
- Inline text input appears with OK/Cancel buttons

### 7.7 Inline edit — save
- Type new value, press Enter OR click checkmark button
- Field updates, success message shown

### 7.8 Inline edit — cancel
- Press Esc OR click X button
- Reverts to original value, no save

### 7.9 Inline edit — empty value rejected
- If new value is empty string, save is rejected (returns false)

### 7.10 Delete album
- Click gear icon on album -> click "delete"
- Album removed from list, success message: "Album deleted"

---

## 8. UI — Status Messages

### 8.1 Success message
- Green alert bar after successful save/delete
- Text: "Album saved" or "Album deleted"

### 8.2 Error message
- Red alert bar on API error
- Text: "Error saving album: {status}" or "Error deleting album: {status}"

### 8.3 Dismiss message
- Click X button on alert -> message disappears

---

## 9. UI — Navigation & Layout

### 9.1 Header brand
- Shows "Spring Music" text with music glyphicon
- Fixed at top of page (navbar-fixed-top)

### 9.2 Info dropdown
- Info icon (glyphicon-info-sign) in top-right of navbar
- Click -> dropdown showing "Profiles: {list}" and "Services: {list}"

### 9.3 Errors page
- Navigate to `#/errors`
- Shows "Force Errors" heading with two buttons: "Kill" and "Throw Exception"

### 9.4 Default route
- Any URL that doesn't match `#/errors` shows the albums view

---

## 10. Database Profile Behavior

### 10.1 No profile (default)
- Uses in-memory H2 database
- JPA repository active
- Data lost on restart

### 10.2 `mysql` profile
- Connects to `jdbc:mysql://localhost/music`
- Uses JPA repository with MySQL dialect

### 10.3 `postgres` profile
- Connects to `jdbc:postgresql://localhost/music`
- Uses JPA repository with Postgres dialect

### 10.4 `mongodb` profile
- Uses MongoAlbumRepository (Spring Data MongoDB)

### 10.5 `redis` profile
- Uses RedisAlbumRepository (Spring Data Redis)

### 10.6 Multiple DB profiles fail
- Starting with more than one DB profile -> IllegalStateException
- Error message lists the conflicting profiles

### 10.7 Profile visible in /appinfo
- Active profile name returned in the `profiles` array

---

## 11. Non-Functional Characteristics

### 11.1 Startup
- Application starts on port 8080 (default)
- Serves requests within seconds of startup

### 11.2 Static assets
- `index.html` served at root
- CSS, JS, and image files served from `/css/`, `/js/`, `/img/`
- Webjar resources served from `/webjars/`

### 11.3 JPA DDL auto-generation
- Tables created automatically (`spring.jpa.generate-ddl: true`)
- No manual schema setup required

### 11.4 Content-Type
- API responses use `application/json`
- Static files use appropriate MIME types
