# Family Project — Specification (May 5, 2026 v0.1)

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

## 2. Platform

### Target Platforms
- Web (initial)
- Mobile iOS / Android (future)

### Suggested Tech Stack (Editable)
- Frontend: Next.js  
- Backend: Node.js (NestJS)  
- Database: PostgreSQL  

---

## 3. User Roles

### 3.1 Admin
- Manage family account  
- Create / remove users  
- Assign roles  
- Configure categories  
- View all data  

### 3.2 User
- Add / edit personal transactions  
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

## 5. Financial Model

### 5.1 Transaction Ownership

- PERSONAL → individual expense  
- SHARED → split across users  

---

### 5.2 Transaction Fields

- amount_original  
- currency_original  
- exchange_rate  
- amount_base  
- category  
- date  
- notes  
- created_by  
- paid_by_user_id  
- ownership_type (PERSONAL / SHARED)  
- status (draft / pending / approved)  

---

### 5.3 Shared Expense Logic

Example:

- Total: 100  
- Paid by: User A  
- Split:
  - User A: 50  
  - User B: 50  

### System Behavior
- Store total transaction  
- Store split breakdown  
- Generate settlement records  

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

---

## 7. Budget Management

### 7.1 Budget Scope
- FAMILY  
- USER  

### 7.2 Features
- Monthly budget  
- Budget vs actual tracking  

### 7.3 Approval Flow
- Family budget created by Admin  
- Optional approval by members  

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
- Exchange rate can be manual (initial)  

---

## 9. Approval Workflow

### Applies To
- Shared transactions (optional)  
- Budgets  

### Status Flow
- Draft → Pending → Approved → Rejected  

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

### Phase 1
- OCR from screenshots / receipts  
- Extract:
  - amount  
  - date  
  - merchant  

### Phase 2
- Auto categorization  

### Phase 3
- Insights and recommendations  
- Price benchmarking  

---

## 12. Data Model (Draft)

### User
- id  
- email  
- password_hash  
- role  
- family_id  

### Family
- id  
- name  
- base_currency  

### Transaction
- id  
- created_by  
- paid_by_user_id  
- ownership_type  
- total_amount  
- currency  
- exchange_rate  
- base_amount  
- category  
- date  
- status  

### TransactionSplit
- id  
- transaction_id  
- user_id  
- share_amount  

### Settlement
- id  
- from_user_id  
- to_user_id  
- amount  
- currency  
- status  
- related_transaction_id  

### Budget
- id  
- scope (USER / FAMILY)  
- user_id  
- category  
- amount  
- month  
- status  

### AuditLog
- id  
- entity_type  
- entity_id  
- action  
- old_value  
- new_value  
- user_id  
- timestamp  

---

## 13. Open Questions (To Be Defined)

### Settlement UX
- Should settlement be:
  - Separate transaction  
  - Or action button (“Settle”)  

### Partial Payments
- Confirm support for partial settlement  

### Category Management
- Shared category list or user-specific  

### Approval Strictness
- Hard approval (block usage)  
- Soft approval (informational only)  

### OCR Input
- Screenshot  
- Camera photo  
- PDF  

---

## 14. Development Phases

### Phase 1
- Authentication  
- Transaction (personal + shared)  
- Split logic  
- Basic dashboard  

### Phase 2
- Settlement  
- Budget  
- Multi-currency  

### Phase 3
- Approval workflow  
- Audit trail  

### Phase 4
- OCR  
- AI features  

---

## 15. Change Log

### 2026-05-05
- Initial structured specification v2 created  



