# Question and Answer Session between AI and me


## Question & Answer List about Coding Style as of May 15, 2026

### Question

1. Your Coding Style Today
2. Your Preferred Control Level
3. Your Biggest Pain Point
4. Your Expected Development Rhythm
5. Your Preferred AI Workflow
6. How Comfortable Are You With Testing?
7. How Much Documentation Discipline Can You Maintain?

### Answer
1. I mainly understand architecture/business logic, but AI writes most code
2. I want AI to generate chunks automatically, but I want to keep my flexibility to review and amend. So the code is required to be ready for human understanding and well structured, instead of No one understanding of AI generation.
3. I worried about: Losing track of architecture; Token/API cost; Maintaining long-term code quality
4. I prefer to have big feature bursts on weekends and rapid experimentation with my family
5. I prefer IDE-integrated AI 
6. Minimal manual testing; But I dont want AI testing to mess up my coding structure, better keep in another folder
7. AI genereate details, I can review strictly docs/specs/changelogs




## Question & Answer at 12:22 AM on May 5, 2026

### Question
1. Data Ownership Logic (VERY important)
Is money:
👤 Individual-based?
👨‍👩‍👧 Family pooled?
🔀 Hybrid?
👉 Example:
If you pay for dinner, is it:
Your expense?
Family expense?
2. Shared vs Personal Transactions
Do you want:
Toggle: Personal / Shared
Or auto rules?
3. Budget Ownership
Budget per:
User?
Category?
Whole family?
4. Currency
Single currency (SGD)?
Multi-currency?
5. Complexity Level
Be honest here:
Option A (Simple - MVP)
    Manual entry only
    No audit logs
    No complex permissions
Option B (Advanced)
    Audit trail
    Approval workflow (e.g. spouse approval)
    Multi-account reconciliation
6. Tech Stack Direction
Since your laptop is limited (I remember this), you probably want:
Frontend:
    Next.js / React
Backend:
    Supabase / Firebase (low infra)
DB:
    PostgreSQL
But confirm:
👉 Do you want low-code speed or full control?
7. AI Integration Level
You mentioned AI updating markdown.
How far do you want to go?
Level 1: Just documentation updates
Level 2: Code generation + tracking
Level 3: Auto feature evolution (very ambitious)

### Answer
1. If I pay for dinner, it shall be my personal expense. If we pay for house rent, it shall be our family expense
2. Tog to Select Personal / Shared, but we set up auto rules to track if other partner has paid back the split cost or not. We need AI OCR to screenshot to get the amount details in case heavily manual input
3. Budget per whole family and individual as well, family shall be managed by primary family member
4. We need to have Multi-currency, as we have SGD, RMB, IDR etc. So we need to get some good practice to see how to manage it 
5. Complexity Level - In this stage, I develop my own application myself, so I try to keep it simpler instead. But not MVP, as we will really use it shortly. Audit trial and approval workflow is needed, especially for submitting split cost, or agreement on monthly bugdet
6. I want full control as I know low-code sooner or later will have limitations. You can recommend me to best practice for personal application.
7. Currently, just let AI develop code for me, but I will include AI agent feature gradually, like OCR to extract data from screenshot, Operating browser/app to record the cost of grocery and benchmark them.