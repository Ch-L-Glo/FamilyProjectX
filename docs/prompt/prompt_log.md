# Prompt Log


## 2026-05-17 4:49 PM
- [My Query] ok, everything looks in order now, but how I can manually see something? to check if it is really working and criteria of working
- You can manually check it in 4 levels.

  1. Check PostgreSQL Is Running

  cd /Users/lichonghua/Documents/VSCODE/ProjectX/FamilyProjectX
  docker compose ps

  Working criteria:

  familyprojectx-postgres   running (healthy)   0.0.0.0:5432->5432/tcp

  2. Check Backend Starts

  cd backend
  mvn spring-boot:run

  Working criteria:

  Tomcat started on port 8080
  Started FamilyFinanceApplication
  HikariPool-1 - Start completed

  That means backend is running and connected to PostgreSQL.

  3. Check APIs In Browser With Swagger

  Open:

  http://localhost:8080/swagger-ui/index.html

  Working criteria:

  - Swagger UI opens in browser.
  - You can see API groups such as auth-controller, family-controller, transaction-controller, settlement-controller, balance-controller, budget-controller, and category-controller.
  - Public endpoints like /api/auth/register and /api/auth/login can be tested without token.
  - Protected endpoints require JWT token.

  Sample Swagger test flow:

  A. Register primary user

  Endpoint:

  POST /api/auth/register

  Request body:

  {
    "email": "primary@example.com",
    "password": "password123",
    "familyName": "My Family",
    "baseCurrency": "SGD"
  }

  Expected result:

  - Response includes token, userId, and email.
  - Copy the token.

  B. Authorize Swagger

  Click Authorize at the top right of Swagger UI.

  Value:

  Bearer {token}

  Example:

  Bearer eyJhbGciOiJIUzI1NiJ9...

  C. Check current user and family

  Endpoint:

  GET /api/auth/me

  Expected result:

  - Response includes userId.
  - Response includes families[0].familyId.
  - Copy familyId for the next steps.

  D. Create category

  Endpoint:

  POST /api/families/{familyId}/categories

  Request body:

  {
    "name": "Groceries",
    "type": "EXPENSE"
  }

  Expected result:

  - Response includes category id.
  - Copy category id.

  E. Create shared transaction with amount split

  This requires two users in the same family. If you only have one user now, test PERSONAL transaction first, or invite/register/accept a second user before testing SHARED.

  Endpoint:

  POST /api/families/{familyId}/transactions

  Request body for SHARED transaction:

  {
    "amountOriginal": 100,
    "currencyOriginal": "SGD",
    "exchangeRate": 1,
    "exchangeRateSource": "MANUAL",
    "categoryId": "{categoryId}",
    "date": "2026-05-17",
    "notes": "Swagger shared grocery test",
    "paidByUserId": "{primaryUserId}",
    "ownershipType": "SHARED",
    "transactionType": "EXPENSE",
    "splits": [
      {
        "userId": "{primaryUserId}",
        "shareAmount": 30
      },
      {
        "userId": "{partnerUserId}",
        "shareAmount": 70
      }
    ]
  }

  Expected result:

  - status is APPROVED.
  - ownershipType is SHARED.
  - amountBase is 100.0000.
  - splits show 30 and 70.

  F. Check settlement generated internally

  Endpoint:

  GET /api/families/{familyId}/settlements

  Expected result:

  - One settlement exists.
  - fromUserId is partnerUserId.
  - toUserId is primaryUserId.
  - amount is 70.0000.
  - status is PENDING.

  G. Check balance

  Endpoint:

  GET /api/families/{familyId}/balances

  Expected result:

  - Primary user pendingReceivable is 70.0000.
  - Partner user pendingPayable is 70.0000.

  H. Create family budget

  Endpoint:

  POST /api/families/{familyId}/budgets

  Request body:

  {
    "scope": "FAMILY",
    "categoryId": "{categoryId}",
    "amount": 800,
    "month": "2026-05"
  }

  Expected result:

  - scope is FAMILY.
  - amount is 800.
  - status is ACTIVE.

  4. Check Data In PostgreSQL With DBeaver

  Recommended GUI: DBeaver Community Edition.

  Connection:

  Host: localhost
  Port: 5432
  Database: family_finance
  Username: family_finance
  Password: family_finance

  Browse:

  Schemas -> public -> Tables

  Working criteria: you should see tables like:

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

  Optional command-line check if needed:

  docker exec -it familyprojectx-postgres psql -U family_finance -d family_finance

  Check registered users:

  select id, email, active from user_accounts;

  Check families:

  select id, name, base_currency from families;

  Check settlements:

  select from_user_id, to_user_id, amount, currency, status from settlements;

  Working criteria from our smoke test:

  amount = 70.0000
  currency = SGD
  status = PENDING

  Practical Working Criteria

  For Phase 1, it is working if:

  - You can register/login and get a JWT token.
  - /api/auth/me returns your user and family.
  - You can create a category.
  - You can invite and accept a family member.
  - You can create a shared transaction.
  - Shared transaction automatically creates settlement.
  - Balance endpoint shows pending payable/receivable.
  - Budget can be created.
  - PostgreSQL tables contain the records.

  The most visual manual check now is Swagger UI for API behavior and DBeaver for database records.

## 2026-05-17 3:49 PM
- [My Query] what shall I do if i want to check function is ready or not?

• To check whether the backend function is ready, do this in order:

  1. Install prerequisites

  You need:

  java -version
  mvn -version

  Backend needs Java 17+. Your current Java is 16, so install Java 17 first, then Maven.
  - [Instruction]
  On macOS, the simplest way is Homebrew.

    1. Check if Homebrew exists

    brew --version

    If it says command not found, install Homebrew first from:

    /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

    2. Install Java 17

    brew install openjdk@17

    3. Link Java 17

    For Apple Silicon Mac:

    echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
    echo 'export JAVA_HOME="/opt/homebrew/opt/openjdk@17"' >> ~/.zshrc
    source ~/.zshrc

    For Intel Mac:

    echo 'export PATH="/usr/local/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
    echo 'export JAVA_HOME="/usr/local/opt/openjdk@17"' >> ~/.zshrc
    source ~/.zshrc

    4. Confirm

    java -version

    You should see something like:

    openjdk version "17.x.x"

    Then install Maven:

    brew install maven
    mvn -version

    After that:

    cd /Users/lichonghua/Documents/VSCODE/ProjectX/FamilyProjectX/backend
    mvn test

  2. Compile the backend

  From project root:

  cd backend
  mvn test

  If this passes, the code compiles and basic Spring test lifecycle is OK.

  3. Start the backend

  mvn spring-boot:run

  By default it expects PostgreSQL here:

  jdbc:postgresql://localhost:5432/family_finance

  If you do not have PostgreSQL running yet, the app may fail at startup. For quick checking, we can add a dev profile using H2
  database so you can test without setting up PostgreSQL first.

  4. Test core flow by API

  Use Postman, Insomnia, or curl:

  register → login → create category → create transaction → check settlements → check balances → create budget

  Recommended next step: I should add a simple dev profile with H2 and a small smoke-test guide, so you can run the backend locally
  without setting up PostgreSQL first.

## 2026-05-17 3:30 PM
- One unresolved item: your answer for authentication question 2 appears copied from the Budget answer. Before coding later, I still need confirmation on whether Phase 1 includes password reset email, or only register/login/me.
  [My Answer]  It needs to include password reset email.

## 2026-05-15 6:20 PM
  - Most of your statement are correct.
  
  Assumptions To Confirm Before Coding

  1. Should Phase 1 exclude Budget completely, despite the development plan mentioning it under Phase 1?
    [My Answer] This was my mistakes, I have updated in development plan. We shall include Budget under Phase 1.

  2. For authentication, should Phase 1 include only:
      - register user
      - login
      - current user profile
        Or should password reset via email also be included immediately?
    [My Answer] This was my mistakes, I have updated in development plan. We shall include Budget under Phase 1.

  3. Should Phase 1 use JWT authentication for the Spring Boot API?
    [My Answer] Yes, JWT authentication shall be enough for long-term purpose.  

  4. For user/family setup, should the first registered user create a Family and become Admin or primary User?
    [My Answer]  the first registered user will need to create family as primary user. Then the primary user is allowed to invite other family members via email.

  5. For transaction status, should Phase 1 allow only APPROVED transactions for now, while keeping the DRAFT/PENDING/APPROVED enum for future approval workflow?
    [My Answer]  For phase 1 only for APPRVED Status. The other status (DRAFT/PENDING/APPROVED) will be implemented in next phase for split bill.

  6. For split logic, should Phase 1 support manual amount-based splits only, or both amount and percentage splits?
    [My Answer]  Both amount split and percentage split.

  7. For settlement generation, should settlement records be generated automatically when a shared transaction is created, or only via explicit POST /settlement/generate?
   [My Answer]  My idea is to let system manage this logic internal, rather than a POST. But as I said, I am layman to develop this app, so I am not sure my idea is best practice. you can provide your point of view.

  8. For partial payments, should Phase 1 model this now, or keep settlement status as simple PENDING/SETTLED first?
   [My Answer]  keep settlement status as simple PENDING/SETTLED first.

## 2026-05-15 6:06 PM
Read the /docs folder first and fully understand the Family Finance system specification.

After that:
1. Summarize the architecture and Phase 1 scope in your own words
2. Confirm any unclear assumptions before coding
3. Then propose the backend Spring Boot modular structure for Phase 1 only

Constraints:
- Follow existing spec strictly (do not invent new features)
- Focus only on Phase 1 vertical slice:
  authentication → transaction → split → settlement → balance
- No AI features yet
- No overengineering

Wait for my confirmation before generating code.
