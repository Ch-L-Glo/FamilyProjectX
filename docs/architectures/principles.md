# Architecture Principles

## Backend
- Modular monolith architecture
- Thin controllers
- Business logic in services
- DTO validation required

## Financial
- Financial data must be traceable
- Settlement history immutable after approval
- Multi-currency stores original values

## AI
- AI outputs are suggestions only
- Human approval before persistence
- AI service separated from core backend

## Development
- Spec-first development
- Small commits
- Reusable module patterns