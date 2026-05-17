# Change Management Log

## 2026-05-17 4:36 PM
- Added Swagger UI/OpenAPI support to the Spring Boot backend.
- Allowed unauthenticated browser access to `/swagger-ui/**` and `/v3/api-docs/**`.
- Added API documentation title and Phase 1 description.
- Updated backend README with Swagger UI URL for manual API checking.

## 2026-05-17 4:44 PM
- Chose DBeaver Community Edition as the local PostgreSQL GUI for easier manual database inspection.
- Updated backend README with DBeaver installation command, PostgreSQL connection details, and Phase 1 tables to inspect.

## 2026-05-17 4:25 PM
- Generated Phase 1 Spring Boot backend scaffold under `/backend`.
- Added modular monolith packages for authentication, family, category, transaction, split, settlement, balance, and budget.
- Implemented JWT authentication, simple password reset token flow, primary-user family creation, family invitation email boundary, shared transaction splits, internal settlement generation, derived balances, and basic budget/category APIs.
- AI, OCR, dashboard, audit trail, and approval workflow remain out of Phase 1 implementation.
- Added local PostgreSQL Docker Compose setup and backend environment reference for development parity with Supabase/PostgreSQL production.
- Added minimal authenticated invitation acceptance endpoint so invited users can join a family and shared transaction settlement can be tested end-to-end.
- Verified local Docker PostgreSQL startup and Spring Boot backend connection.
- Smoke-tested Phase 1 flow: register primary user, create family/category, register invited user, accept invitation, create shared transaction, verify settlement, verify balance, and create budget.


## 2026-05-15 5:05 PM
- Initial structured specification v1.0 created  
