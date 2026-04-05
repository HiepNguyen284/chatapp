# Contributor Guide

This guide helps coding agents and contributors work safely in the Chat App backend.

## Table of Contents

1. Policies & Mandatory Rules
2. Project Structure Guide
3. Operation Guide

## Policies & Mandatory Rules

### Coding Rules

- Prefer Kotlin for new backend files (`.kt`), while keeping interoperability with existing Java code.
- Use constructor injection only (no field injection).
- Keep API behavior backward-compatible unless the task explicitly asks for breaking changes.
- Do not hard-code secrets or tokens in source code, tests, or docs.

### Container Runtime Rule

- For local container commands, check `podman` first and fallback to `docker`.

### Configuration Rules

- Keep runtime configuration environment-driven via `src/main/resources/application.yml`.
- Keep default values safe for local development.
- If env variables change, update `.env.example` in the same change.

## Project Structure Guide

### Repo Layout

- `src/main/java+kotlin/`: Application source (mixed Java and Kotlin).
- `src/main/resources/application.yml`: Spring runtime configuration.
- `compose.yml`: Local stack (Caddy, app, Postgres, Redis, VersityGW).
- `Dockerfile`: Production-style backend image build.
- `Caddyfile`: Reverse proxy routing (`/api`, `/ws`, `/storage`).

### Tech Stack

- Spring Boot `4.0.5`, Kotlin `2.2.21`, Java `21`.
- PostgreSQL `18`, Redis `8`, S3-compatible storage (VersityGW), Caddy `2`.
- STOMP WebSocket, Firebase Cloud Messaging.
- Gradle Kotlin DSL (`build.gradle.kts`).

## Operation Guide

### Build and Test

- Run tests with `./gradlew test`.
- Build artifact with `./gradlew bootJar`.

### Local Stack

- Start services with `podman compose up -d` (or `docker compose up -d`).
- Stop services with `podman compose down` (or `docker compose down`).

### Change Checklist

- Keep changes minimal and scoped to the task.
- Update docs/config samples when behavior or env keys change.
- Validate build/test status before handoff when code behavior is changed.
