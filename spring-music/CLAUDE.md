# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
export JAVA_HOME=/opt/homebrew/Cellar/openjdk@21/21.0.11/libexec/openjdk.jdk/Contents/Home

# Build the jar
./gradlew clean assemble

# Run tests (self-contained via WireMock, no external dependencies)
./gradlew test

# Run locally (requires album-service running on port 8081 first)
java -jar build/libs/spring-music-1.0.jar
```

Requires JDK 21. Uses Spring Boot 3.2.5 with Gradle 8.14.1 (wrapper included).

## Architecture

This is the **monolith shell** — it serves the frontend and proxies album CRUD requests to the extracted `album-service`. The multi-backend CF support (MongoDB, Redis, CF environment detection) has been removed per decision D-02.

### Strangler Fig Proxy Pattern

`AlbumController` is a thin proxy that forwards all `/albums` requests to the album-service via `RestTemplate`. The album-service URL is configured via `album-service.url` property (defaults to `http://localhost:8081`).

### REST API (proxied to album-service)

| Method | Path | Action |
|--------|------|--------|
| GET | /albums | List all |
| GET | /albums/{id} | Get one |
| PUT | /albums | Create |
| POST | /albums | Update |
| DELETE | /albums/{id} | Delete |

### Other Endpoints (served locally)

- `/appinfo` — active Spring profiles
- `/errors/kill`, `/errors/fill-heap`, `/errors/throw` — chaos-engineering hooks

### Frontend

Static AngularJS 1.2 app served from `src/main/resources/static/`. Uses relative URL `albums` via `$resource` — the proxy is transparent to the frontend.

### Testing

- **Characterization tests** use WireMock to simulate the album-service. They pin the proxy's pass-through behavior without requiring the real service.
- Run both this project and `../album-service` tests to verify the full contract: `../test-all.sh`

## Codebase Quirks

- PUT creates, POST updates — reversed from REST conventions. Preserved for backward compatibility.
- `Album` is a plain DTO in this project (the JPA entity lives in album-service).
- `ErrorController` endpoints are intentional chaos-engineering hooks, not bugs.
