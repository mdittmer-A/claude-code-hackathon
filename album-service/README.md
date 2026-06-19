# Album Service

Extracted microservice owning the Album Catalog capability (user stories US-01 through US-05). This service was cut from the [Spring Music](https://github.com/rishikeshradhakrishnan/spring-music) monolith using the strangler-fig pattern.

## Prerequisites

- JDK 21 (`brew install openjdk@21`)

## Build & Run

```bash
export JAVA_HOME=/opt/homebrew/Cellar/openjdk@21/21.0.11/libexec/openjdk.jdk/Contents/Home

# Run tests
./gradlew test

# Build
./gradlew clean assemble

# Run on port 8081
./gradlew bootRun
```

## API Contract

All endpoints are under `/albums`. The HTTP verb semantics are preserved from the original monolith (PUT creates, POST updates).

| Method | Path | Description | Success | Error |
|--------|------|-------------|---------|-------|
| GET | `/albums` | List all albums | 200 + JSON array | — |
| GET | `/albums/{id}` | Get album by ID | 200 + JSON (or empty for non-existent) | — |
| PUT | `/albums` | Create a new album | 200 + created album with generated ID | 400 if validation fails |
| POST | `/albums` | Update an existing album | 200 + updated album | 400 if validation fails |
| DELETE | `/albums/{id}` | Delete an album | 200 | — |

### Album Schema

```json
{
  "id": "uuid-string (generated)",
  "title": "string (required)",
  "artist": "string (required)",
  "releaseYear": "string, 4 digits 1000-2999 (required)",
  "genre": "string (required)",
  "trackCount": 0
}
```

### Validation Rules

- `title`, `artist`, `genre`: must not be blank
- `releaseYear`: must not be blank, must match `^[1-2]\d{3}$`
- Invalid requests return `400 Bad Request`

## Persistence

- Spring Data JPA with H2 in-memory database
- On first startup (empty DB), seeds 29 albums from `src/main/resources/albums.json`
- UUIDs generated via custom Hibernate `IdentifierGenerator`

## Testing

Contract tests (`AlbumControllerContractTest`) verify the complete API surface:

- CRUD lifecycle
- Validation rejection for each required field
- Non-existent ID behavior (returns empty, not 404)
- Field completeness in JSON responses

Run with: `./gradlew test` (13 tests total)

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `8081` | Port this service listens on |
| `spring.datasource.url` | `jdbc:h2:mem:albumdb` | Database URL |
