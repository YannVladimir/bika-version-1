# Bika — Document Management System

Bika is an enterprise document management system that links digital and physical archives: Company → Department → User hierarchy, custom document types with configurable metadata, full-text/metadata search via Elasticsearch, a personal drive, and physical storage location tracking (office → cupboard → drawer → folder). See `autocoding/overview.md` for the full feature spec.

## Stack

- **Backend**: Spring Boot 3.2 (Java 17), PostgreSQL + Flyway migrations, Redis, Elasticsearch, MinIO for object storage, JWT auth.
- **Frontend**: React 18 + TypeScript (Create React App), MUI.

## Security note on this cleanup pass

This repo had real secrets committed in `backend/src/main/resources/application.properties` and `application.yml` (database password, mail server password, and a hardcoded fallback JWT signing secret), plus duplicate copies of the same secrets baked into committed build output (`backend/target/...`) and a setup doc. All of these have been **removed from the entire git history** (not just deleted going forward) and force-pushed, with explicit approval. **If this app is deployed anywhere using these credentials, rotate the database password, the mail password, and the JWT secret** — history rewriting doesn't undo the fact they were public.

Going forward, `application.yml`/`application.properties` are gitignored. Copy `backend/src/main/resources/application.yml.example` to `application.yml` and fill in real values.

## Setup

### Backend

```bash
cd backend
cp src/main/resources/application.yml.example src/main/resources/application.yml
# edit application.yml with real DB/mail/MinIO/JWT values
mvn spring-boot:run
```

Requires PostgreSQL, Redis, Elasticsearch, and MinIO running (see `application.yml.example` for default local ports).

### Frontend

```bash
cd frontend
cp .env.example .env   # only needed if the API isn't at the default localhost:8081
npm install
npm start
```

## Tests

```bash
cd backend && mvn test
cd frontend && npm test
```

**Backend test status — read before assuming CI is fully green.** The test suite (117 tests across unit and integration tests) had a real bug blocking *all* integration tests: the test Spring profile (`application-test.properties`/`.yml`) didn't configure `spring.mail.*` or the `email.*` properties that `EmailService`/`PasswordResetService` need, so `@SpringBootTest` failed to even load its `ApplicationContext` for 75 of 117 tests. Fixed by adding dummy test values for those properties.

With that fixed, a second, deeper issue surfaced: `Project.status` and `Task.status` use native PostgreSQL enum types (`CREATE TYPE project_status AS ENUM (...)`, matching real Flyway migrations) via Hibernate's `@JdbcTypeCode(SqlTypes.NAMED_ENUM)` — which the H2 in-memory database used for tests can't represent, so creating the `projects`/`tasks` tables fails under H2. Because most integration tests share teardown logic that touches those tables, this one incompatibility cascades into failures across most integration test classes, even ones unrelated to projects or tasks.

This is a test-infrastructure limitation, not a case for changing the entity mappings (they correctly match the real production schema). The idiomatic fix is to run integration tests against a real throwaway PostgreSQL instance via [Testcontainers](https://testcontainers.com/) instead of H2 — not implemented here because this environment has no running Docker daemon to verify it against, and an unverified fix for a problem this central isn't worth the risk of shipping something subtly wrong. Recommended next step for whoever picks this up.

Pure unit tests (service tests using Mockito, no Spring context) are unaffected and pass.

### Frontend

Added `src/utils/roleUtils.test.ts` (24 tests, all passing) covering the role-based access control helpers — the only pure, dependency-free logic in the frontend at the time of this pass. Also fixed `services/api/client.ts`, which hardcoded the backend URL (`http://localhost:8081/api`) instead of reading it from `REACT_APP_API_URL`, making it impossible to point the frontend at a different environment without editing source.

## Continuous integration

`.github/workflows/ci.yml` runs the backend test suite (JDK 17 + Maven) and the frontend test suite + production build (Node + npm) on every push. The backend job will currently show failures for the reason documented above — that's expected until the H2/Testcontainers issue is addressed, not a regression from this pass.

## Build tooling note

Both `pom.xml` and `build.gradle` existed for the backend, seemingly from parallel/duplicate scaffolding. `build.gradle` and the Gradle wrapper cache have been removed — `target/` (the real, populated build output) confirmed Maven is what's actually been used.

## License

Proprietary — this is Bika DMS, a commercial product built for C-Note Ltd, not an open-source project.
