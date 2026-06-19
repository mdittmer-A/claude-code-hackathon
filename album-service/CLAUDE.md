# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
export JAVA_HOME=/opt/homebrew/Cellar/openjdk@21/21.0.11/libexec/openjdk.jdk/Contents/Home

# Build
./gradlew clean assemble

# Run tests (contract tests, self-contained with H2)
./gradlew test

# Run locally on port 8081
./gradlew bootRun
```

Requires JDK 21. Uses Spring Boot 3.2.5 with Gradle 8.14.1.

## Architecture

Extracted Album Catalog microservice. Owns all album CRUD and persistence. The monolith (`../spring-music`) proxies to this service.

### Packages

- `com.catalog.album.domain` — `Album` JPA entity, `RandomIdGenerator` (UUID-based Hibernate ID generator)
- `com.catalog.album.repository` — `AlbumRepository` (Spring Data JPA interface)
- `com.catalog.album.web` — `AlbumController` (REST endpoints)
- `com.catalog.album.seed` — `AlbumDataSeeder` (loads `albums.json` on first startup when DB is empty)

### API Contract

| Method | Path | Action |
|--------|------|--------|
| GET | /albums | List all albums |
| GET | /albums/{id} | Get by ID (returns null/empty for non-existent) |
| PUT | /albums | Create new album (generates UUID) |
| POST | /albums | Update existing album |
| DELETE | /albums/{id} | Delete album |

PUT creates, POST updates — preserved from the original monolith contract.

### Validation

- `title`, `artist`, `genre`: `@NotBlank`
- `releaseYear`: `@NotBlank` + `@Pattern("^[1-2]\\d{3}$")`
- Invalid input returns 400 Bad Request

### Testing

Contract tests (`AlbumControllerContractTest`) verify the full API shape using MockMvc against an in-memory H2 database. These are the source of truth for the API contract that the monolith proxy relies on.
