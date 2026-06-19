#!/bin/bash
set -e

export JAVA_HOME=/opt/homebrew/Cellar/openjdk@21/21.0.11/libexec/openjdk.jdk/Contents/Home

echo "=== Running album-service tests ==="
cd "$(dirname "$0")/claude-code-hackathon/album-service"
./gradlew test

echo ""
echo "=== Running spring-music (monolith) tests ==="
cd "$(dirname "$0")/../claude-code-hackathon/spring-music"
./gradlew test

echo ""
echo "=== ALL TESTS GREEN ==="
