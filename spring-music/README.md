# Spring Music — Monolith (Proxy Shell)

This is the modernized monolith from the [Spring Music](https://github.com/rishikeshradhakrishnan/spring-music) sample app. It has been refactored as part of a strangler-fig service extraction: the Album CRUD capability has been extracted into `../album-service/`, and this project now acts as a **proxy shell** that forwards album requests to the new service while continuing to serve the frontend and auxiliary endpoints.

## Prerequisites

- JDK 21 (`brew install openjdk@21`)
- Album service running on port 8081 (for runtime; tests are self-contained)

## Build & Run

```bash
export JAVA_HOME=/opt/homebrew/Cellar/openjdk@21/21.0.11/libexec/openjdk.jdk/Contents/Home

# Run tests (self-contained via WireMock)
./gradlew test

# Build
./gradlew clean assemble

# Run (requires album-service on port 8081)
java -jar build/libs/spring-music-1.0.jar
```

## Architecture

```
┌─────────────────────────────────────────────────┐
│  Browser (AngularJS 1.2)                        │
│  GET/PUT/POST/DELETE /albums (relative URL)      │
└──────────────────────┬──────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────┐
│  spring-music (this project) — port 8080        │
│                                                 │
│  AlbumController ──► RestTemplate proxy ──────────┐
│  InfoController (local)                         │ │
│  ErrorController (local)                        │ │
│  Static frontend (webjars)                      │ │
└─────────────────────────────────────────────────┘ │
                                                    │
┌───────────────────────────────────────────────────▼─┐
│  album-service — port 8081                          │
│                                                     │
│  AlbumController → AlbumRepository → H2 (JPA)      │
│  Seed data from albums.json                         │
└─────────────────────────────────────────────────────┘
```

## What Was Removed (D-02: Drop Multi-Backend)

- MongoDB repository and Spring Data Mongo dependency
- Redis repository and config
- `SpringApplicationContextInitializer` (Cloud Foundry environment detection)
- `java-cfenv` dependency
- Profile-based database switching (`mysql`, `postgres`, `mongodb`, `redis`)
- JPA/H2 persistence layer (moved to album-service)

## Testing

The characterization test suite uses **WireMock** to simulate the album-service, making tests fully self-contained:

- `AlbumControllerCharacterizationTest` — pins the proxy pass-through behavior (8 tests)
- `ApplicationTests` — context loads (1 test)

Run with: `./gradlew test`

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `album-service.url` | `http://localhost:8081` | Base URL of the extracted album service |
| `server.port` | `8080` | Port this proxy listens on |
