# FamilyProjectX Backend

Spring Boot modular monolith for Phase 1.

## Scope

- JWT authentication
- Register, login, current user, password reset token flow
- Family creation and member invitation
- Invitation acceptance
- Category setup
- Personal, family, and shared transactions
- Amount and percentage splits
- Internal settlement generation
- Settlement list and mark-settled
- Derived balance calculation
- Basic monthly budgets

## Run

Requires Java 17+ and Maven.

Start local PostgreSQL from the repository root:

```bash
docker compose up -d postgres
```

Run the backend:

```bash
cd backend
mvn spring-boot:run
```

Configuration is environment-driven in `src/main/resources/application.yml`.

For local development, the defaults match `docker-compose.yml`:

```text
DB_URL=jdbc:postgresql://localhost:5432/family_finance
DB_USERNAME=family_finance
DB_PASSWORD=family_finance
```

Use `backend/.env.example` as the reference for deployment variables on Fly.io or DigitalOcean. Supabase should provide the production PostgreSQL connection values.

## Database GUI

Use DBeaver Community Edition to inspect local PostgreSQL without `psql`.

Install:

```bash
brew install --cask dbeaver-community
```

Create a new PostgreSQL connection:

```text
Host: localhost
Port: 5432
Database: family_finance
Username: family_finance
Password: family_finance
```

Browse tables under:

```text
Schemas -> public -> Tables
```

Phase 1 tables to inspect:

```text
user_accounts
families
family_members
family_invitations
categories
transactions
transaction_splits
settlements
budgets
password_reset_tokens
```

## Smoke Check

After the backend starts on port `8080`, open Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

You can also register the first user with curl:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "primary@example.com",
    "password": "password123",
    "familyName": "My Family",
    "baseCurrency": "SGD"
  }'
```

The response should include a JWT token and user id.

The first registered user is created as the family `PRIMARY` member. A primary member can invite another user:

```bash
curl -X POST http://localhost:8080/api/families/{familyId}/invitations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {primaryToken}" \
  -d '{"email": "partner@example.com"}'
```

The current local email sender writes invitation/reset links to the backend log. After the invited user registers and receives the token, they can accept:

```bash
curl -X POST http://localhost:8080/api/families/{familyId}/invitations/accept \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {partnerToken}" \
  -d '{"token": "{invitationToken}"}'
```
