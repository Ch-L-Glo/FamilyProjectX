# Family Project — Specification (May 15, 2026 v1.0)

## 1. Background

You are an IT consultant from top-tier firms (e.g. McKinsey Digital, QuantumBlack, BCG X).  
This project is a family finance tracking and budget management system for multi-user households.

### Objectives
- Track personal and shared financial transactions  
- Manage family and individual budgets  
- Provide financial visibility  
- Enable settlement tracking  
- Support multi-currency  

---

## 2. Technical Requirement

### Target Platforms
- Web app on mobile phone and Desktop

### Tech Stack

### Architecture Overview
Hybrid architecture with separation between core system and AI services:
Frontend → Backend (Core API) → AI Service → Database 

### Frontend
- Framework: Next.js
- Purpose:
  - User interface
  - Forms (transactions, budget)

### Backend (Core System)
- Framework: Java Spring Boot (Modular, rather than microservice)
- Responsibilities:
  - Authentication & authorization
  - Transaction management
  - Split & settlement logic
  - Budget management
  - Approval workflow
  - Audit logging
  - Data validation
  - API gateway to AI services

---

### AI Service (Independent Microservice)

- Framework: Python (FastAPI)
- Responsibilities:
  - OCR processing (receipts, screenshots)
  - Data extraction (amount, date, merchant)
  - Auto categorization
  - Agent workflows (LangGraph)

- AI / ML Layer
  - LLM Integration (API-based)
  - OCR Engine (PaddleOCR / Tesseract)
  - AI Workflow Layer (LangGraph)

---

### Database
- PostgreSQL

### Service Communication

#### Pattern
- REST API
- Internal service-to-service calls

#### Example Flow

1. Login
2. User create income or expense /uploads receipt via frontend  
3. Request sent to backend  
4. Backend record / forwards image to AI service if user uploads receipt
5. Split calculation if needed
6. Settlement Process generated if needed
7. AI service processes OCR and returns structured data if user uploads receipt
8. Backend validates and stores transaction in database  

---

### Development Pattern

1. Spec-First Development: Please ask me about requirement specifications everytime if any functions are unclear to develop. e.g., requirement specifications, Module boundary, API, and logic rules, etc.
2. Golden Path Templates: Follow first module we build together as practice to develop other module and functions.
3. Context Alignment: Document all context during development, specify all changes in logs as well for huamn understanding, review and future restructure.
4. Repo as Memory: Follow what we have 'docs' folder, and update them to ask me to review
  /docs/spec
  /architecture
  /decisions
  /prompts
  /features
5. Commit Small, Commit Often: 

---

### Deployment

- Frontend: Next.js; Deploy on Cloudflare Pages
- Backend: Java Spring Boot (Modular, rather than microservice); Deploy on Fly.io (Free tier to start) / Digital Ocean
- AI Service: Python Langgraph; Deploy on Fly.io (Free tier to start) / Digital Ocean
- Database: PostgreSQL; Deploy on Supabase

## 3. User Roles

### 3.1 Admin
- Manage family account  
- Create / remove primary users  
- Assign roles 
- Configure categories  
- View all data  

### 3.2 primary User
- invite normal user  
- Add / edit personal transactions 
- Add / edit family transactions  
- Participate in shared transactions  
- View dashboard  
- Manage family budget  
- Manage personal budget  

### 3.3 normal User
- Add / edit personal transactions 
- Add / edit family transactions  
- Participate in shared transactions  
- View dashboard  
- Manage personal budget  

---

## 4. Authentication

### Features
- Email + password login  
- Password reset via email  

### Future
- Google / Apple login  

---

## Financial Data Integrity Principles

- Financial records should be historically traceable
- Important financial updates should preserve previous values
- Deletion should use soft delete whenever possible
- Settlement history should remain immutable after approval

---

## 5. Transaction Model

### 5.1 Transaction Ownership

- PERSONAL → individual (INCOME / EXPENSE / TRANSFER)
- FAMILY → family (INCOME / EXPENSE / TRANSFER)
- SHARED → split across users  

---

### 5.2 Transaction Fields

- amount_original  
- currency_original  
- exchange_rate  
- exchange_rate_source
- amount_base
- category
- date  
- notes  
- is_active
- created_by  
- created_date
- modified_by 
- modified_date 
- paid_by_user_id  
- ownership_type (PERSONAL / FAMILY/ SHARED)  
- status (draft / pending / approved)  

---

### 5.3 Shared Expense Logic

Example:

- Total: 100  
- Paid by: User A  
- Configured by: User A, 70% to be shared with user B 
- Split:
  - User A: 30  
  - User B: 70  

### System Behavior
- Store total transaction  
- Store split breakdown  
- Generate settlement records  

### Transaction Revision Strategy

- All updates to approved transactions must create a new revision record
- Original transaction remains immutable after approval
- Revisions are linked via transaction_id

---

## 6. Settlement Management

### Purpose
Track who owes whom

### Fields
- from_user_id  
- to_user_id  
- amount  
- currency  
- related_transaction_id  
- status (pending / settled)  

### Rules
- Support partial payments  
- Settlement linked to transaction 

### Balance Calculation Rule

User balance is derived from:

- Transactions (income/expense)
- Transaction splits
- Settlement records

Balance is NOT stored directly in database.

---

## 7. Budget Management

### 7.1 Budget Scope
- FAMILY  
- USER  

### 7.2 Features
- Monthly budget  
- Budget vs actual tracking  

### 7.3 Approval Flow
- Family budget created by primary users  
- Optional edit by normal users 

---

## 8. Multi-Currency

### Strategy

Each transaction stores:
- original currency  
- exchange rate  
- base currency amount  

### Base Currency
- Defined at Family level (e.g. SGD)  

### Notes
- Avoid recalculating historical data  
- Exchange rate can be extracted from Yahoo Finance via API on daily basis as per transaction requirement

---

## 9. Approval Workflow

### Applies To
- Shared transactions (optional) 

### Status Flow
- Draft → Pending → Approved / Edit and Approved/ Send-back  

---

## 10. Audit Trail

### Purpose
Track all changes

### Fields
- entity_type  
- entity_id  
- action (create / update / delete)  
- old_value  
- new_value  
- user_id  
- timestamp  

---

## 11. AI Features (Planned)

## AI Constraint Principle

- AI must never directly write to database
- AI outputs are always "suggestions"
- Human approval required before persistence

### AI Phase 1
- OCR from screenshots / receipts  
- Extract:
  - amount  
  - date  
  - merchant  
  - categorization  

### AI Phase 2
- Price benchmarking  

---

## 12. Data Model (Draft)

### User
- uuid  
- email  
- password_hash  
- role  
- family_id  

### Family
- uuid  
- name  
- base_currency  

### FamilyMember
- uuid
- family_id
- user_id
- role
- joined_date
- status

### Transaction
- uuid  
- amount_original  
- currency_original  
- exchange_rate 
- exchange_rate_source   
- amount_base
- entry_source
- category  
- date  
- notes  
- is_active
- created_by  
- created_date
- modified_by 
- modified_date  
- paid_by_user_id  
- ownership_type (PERSONAL / FAMILY/ SHARED)  
- status (draft / pending / approved) 

### TransactionRevision
- uuid
- transaction_id
- previous_state
- new_state
- changed_by
- timestamp

### TransactionSplit
- uuid  
- transaction_id  
- user_id  
- share_amount  

### Category
- uuid
- family_id
- name
- type (INCOME / EXPENSE / TRANSFER)
- is_active

### Settlement
- uuid  
- from_user_id  
- to_user_id  
- amount  
- currency  
- status  
- related_transaction_id  

### Budget
- uuid  
- scope (USER / FAMILY)  
- user_id  
- category  
- amount  
- month  
- status  

### AuditLog
- uuid  
- entity_type  
- entity_id  
- action  
- old_value  
- new_value  
- user_id  
- timestamp  
---

## 13. API (Draft) 

### API Design Rules

- All endpoints are family-scoped
- All endpoints require authentication
- All financial operations must include family_id context
- No direct cross-family access allowed

### Example API
- POST /auth/login
- POST /transaction
- GET /transaction
- POST /transaction/split
- POST /settlement/generate
- GET /settlement/balance
