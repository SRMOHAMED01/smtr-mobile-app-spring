# SMTR — Technical and Logical Report

## 1. Architectural overview

SMTR is a stateless REST backend for fleet operations. A future mobile or web client authenticates with `POST /api/auth/login`, receives a JWT, then calls protected endpoints with `Authorization: Bearer <token>`.

The implementation follows a conventional Spring Boot layered architecture:

- **Controllers** expose HTTP endpoints and translate JSON requests/responses.
- **Services** contain transactional business rules for departures, returns and photos.
- **Repositories** are Spring Data JPA gateways to PostgreSQL.
- **Entities** define the persistence model managed by Hibernate.
- **DTO records** isolate public API payloads from entities.
- **Security** authenticates JWTs and enforces `AGENT` / `ADMIN` permissions.

`docker compose.yaml` starts PostgreSQL and the backend. Configuration is supplied through environment variables; the JWT secret is deliberately required and is not stored in source code.

## 2. Domain workflow

### Departure (`sortie`)

1. An authenticated agent selects a truck, driver, mission type and mileage.
2. The service verifies that the truck is not already `EN_MISSION` and the driver has no open mission.
3. The submitted checklist must contain every mandatory document exactly once. An absent document requires a justification.
4. A `Mission` is created with status `OUVERTE`; its `DocumentCheck` records are saved in the same transaction.
5. The truck becomes `EN_MISSION` and audit events are recorded.

The current functional policy accepts an absent document when a justification is supplied. The `REFUSEE` status exists in the model but is not yet exposed by the workflow; introduce an explicit approval/refusal request only after the business rule is confirmed.

### Return (`entrée`)

1. The same agent who created the mission (or an administrator) submits the return mileage and observations.
2. The mission must be `OUVERTE` and the return mileage must be at least the departure mileage.
3. The mission becomes `TERMINEE`; anomalies and maintenance need are recorded.
4. The truck returns to `AU_PARC` and a status-change audit event is persisted.

### Photos

Photos are stored in PostgreSQL as `bytea` data, linked to a mission and optionally a document check. The backend accepts only non-empty JPEG/PNG files whose binary content is decodable as an image. A document photo must reference a document check belonging to the same mission.

This strategy is suitable for a small educational or internal deployment. For high volume, keep image metadata in PostgreSQL and store binary files in an object store, retaining the same service interface.

## 3. Entity model and relationships

| Entity | Role | Main relationships |
|---|---|---|
| `User` | Application account, role and password hash | One user creates many missions and document checks; performs many audit events. |
| `Role` | `AGENT` or `ADMIN` | Stored on `User`; translated to Spring authority `ROLE_<role>`. |
| `Truck` | Fleet vehicle and current availability | One truck has many missions; status is `AU_PARC` or `EN_MISSION`. |
| `Driver` | Driver identity | One driver may be assigned to many missions over time. |
| `Mission` | Departure/return business aggregate | References one truck, driver and agent; owns checks, photos and audit events. |
| `DocumentType` | Configurable document definition | Referenced by many checks; may be mandatory. |
| `DocumentCheck` | Result of one document inspection | Belongs to one mission and one type; records the checking agent. |
| `Photo` | Evidence image | Belongs to one mission and optionally one document check. |
| `MissionAudit` | Immutable mission-action trace | Stores actor, action, previous/new status, details and timestamp. |

`Truck.version` uses optimistic locking. Concurrent updates to the same truck therefore fail with HTTP `409` rather than silently creating inconsistent availability.

## 4. Security architecture

- Login delegates password verification to Spring Security and BCrypt.
- `JwtService` signs a token containing the username, role and user id; tokens expire after the configured duration.
- `JwtAuthFilter` parses a bearer token and places the corresponding `UserPrincipal` in the security context.
- Controllers use `@PreAuthorize` for coarse role rules. `MissionAccessService` adds domain-level ownership: an `AGENT` may access only missions it created; an `ADMIN` has fleet-wide access.
- Invalid or missing credentials receive HTTP `401`; insufficient role/ownership receives HTTP `403`.
- CORS uses an explicit configurable origin list, never a wildcard with credentials.

## 5. API summary

| Endpoint | Access | Purpose |
|---|---|---|
| `POST /api/auth/login` | Public | Obtain JWT. |
| `GET, POST, PUT, DELETE /api/trucks` | Read: agent/admin; write: admin | Fleet management. |
| `GET, POST, PUT, DELETE /api/drivers` | Read/write: agent/admin; delete: admin | Driver management. |
| `GET, POST, DELETE /api/document-types` | Read: agent/admin; write: admin | Checklist configuration. |
| `POST /api/missions/sortie` | Agent/admin | Register a controlled departure. |
| `POST /api/missions/{id}/entree` | Mission owner/admin | Register the return. |
| `GET /api/missions/{id}` | Mission owner/admin | Retrieve one mission. |
| `GET /api/missions`, `/history`, `/anomalies` | Admin | Fleet-wide reporting. |
| `POST /api/missions/{id}/photos` | Mission owner/admin | Upload mission evidence. |
| `GET /api/photos/{id}` | Mission owner/admin | Read mission evidence. |

## 6. Audit findings and actions

### Corrected

- Docker build context and Java 17/21 mismatch.
- Duplicate and contradictory database properties.
- Committed JWT secret fallback, always-on demonstration accounts and permissive CORS policy.
- Missing explicit 401/403 security responses.
- Negative mileage and unbounded text payloads.
- Duplicate document checks and concurrent driver assignments.
- Cross-mission document-photo association.
- Arbitrary/non-image upload acceptance.
- Cross-agent access to mission details, returns and photos.
- Missing mission audit trail and optimistic concurrency control on trucks.
- Exposure of internal exception messages in HTTP 500 responses.

### Remaining planned work

1. Add Flyway migrations and change production DDL mode to `validate`.
2. Add unit/integration tests for all mission transitions, ownership and photo validation.
3. Add pagination and projection queries for mission history/anomalies to prevent large-list and N+1 costs.
4. Decide the explicit refusal/approval policy for non-compliant documents, then make `REFUSEE` a real transition.
5. Add user administration and replace development seed accounts before deployment.
6. Use object storage when image volume requires it.

## 7. Mapping approach

The existing static methods on response records are appropriate for the current small DTO set. MapStruct becomes useful only when mapping rules multiply across modules; adding it now would introduce a dependency without reducing meaningful complexity. Repository projections and dedicated query methods should be introduced first for reporting endpoints.
