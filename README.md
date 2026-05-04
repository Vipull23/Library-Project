# Digital Library — Backend System

A Spring Boot REST API that manages a digital library — students borrow and return books, authors publish them, and every transaction is tracked end to end. The project uses raw JDBC (no JPA/ORM magic) so the data layer is explicit and visible, with custom validation, a clean service-repository architecture, and a working fine-calculation engine for overdue books.

---

## Table of Contents

1. [What this system does](#what-this-system-does)
2. [How a request flows through the system](#how-a-request-flows-through-the-system)
3. [The three core workflows](#the-three-core-workflows)
4. [Architecture](#architecture)
5. [File-by-file walkthrough](#file-by-file-walkthrough)
6. [API reference](#api-reference)
7. [Data models](#data-models)
8. [Design decisions worth noting](#design-decisions-worth-noting)

---

## What this system does

Three kinds of actors interact with the system:

- **Students** — register with a name, email, mobile number, address, and date of birth. The system validates that they are at least 8 years old before creating an account.
- **Authors** — are not registered independently. They are created on-the-fly the first time one of their books is added. If the same author (matched by email + mobile) tries to publish a second book, the system reuses the existing author record instead of creating a duplicate.
- **Books** — are created with a genre type (`SCI_FI`, `COMIC`, `NOVEL`, `DRAMA`, `CODING`, `BIOGRAPHY`), a price, a publisher, and a description.

Once students and books exist, the **transaction engine** handles three operations: issuing a book to a student, renewing an existing issue, and returning it. On return, it calculates a fine based on how many days the book was held, minus the amount already paid.

---

## How a request flows through the system

Every request enters through a `@RestController`, gets passed to a `@Service` that owns the business logic, and the service calls one or more `@Repository` classes that talk directly to MySQL via `JdbcTemplate`. There is no ORM. Every SQL query is written by hand.

```
HTTP Request
     │
     ▼
  Controller          (validates input shape, maps to/from response objects)
     │
     ▼
  Service             (owns business rules — does author exist? is book available?)
     │
     ▼
  Repository          (raw JDBC — INSERT, SELECT, UPDATE against MySQL)
     │
     ▼
  MySQL Database      (jbdl8_library)
```

Responses are always structured through a shared `Response` base class that carries `status` and `message` fields. Each endpoint extends this with whatever extra data it needs to return.

---

## The three core workflows

### Creating a book

The interesting part here is author deduplication. The service doesn't blindly insert. It first queries the `Author` table by email and mobile number. If a record comes back, the author already exists and we skip the insert. If the query throws (no rows found — `JdbcTemplate.queryForObject` throws `EmptyResultDataAccessException` on zero results), we catch that and create the author.

This means author creation is handled through exception-driven control flow, which is a known trade-off of `queryForObject`. A cleaner production approach would use `query()` and check if the list is empty — but the current behaviour works correctly.

### Creating a student

Student creation uses `SimpleJdbcInsert` instead of a manual `INSERT` statement. This lets Spring handle the generated-key retrieval automatically — after inserting the student row, the auto-incremented `id` comes back, and we use it immediately to insert the student's address into the `Address` table as a separate operation.

The student's date of birth goes through a custom validation annotation (`@ValidAge`) before the request even reaches the service layer. If the DOB is in the future, or if the student would be under 8 years old, the request is rejected at the controller boundary with a validation error.

### Issuing, renewing, or returning a book

A single `POST /transaction/book/initiate` endpoint handles all three operations, differentiated by the `requestType` field in the request body (`ISSUE`, `RENEW`, or `RETURN`).

- **ISSUE**: Updates the book's `STUDENT_ID` column to claim it, then inserts a transaction record.
- **RENEW**: Updates the existing transaction row's type and timestamp.
- **RETURN**: Clears the book's `STUDENT_ID`, calculates a fine, and updates the transaction row with the final cost.

Fine calculation fetches the original `ISSUED_TIME` from the transaction table, computes the number of days elapsed, multiplies by 2, and subtracts the amount the student already paid upfront. The result is the additional charge owed.

---

## Architecture

```
DigitalLibrary/
└── src/main/java/org/gfg/DigitalLibrary/
    │
    ├── controller/          HTTP boundary — three controllers, one per domain
    │   ├── BookController
    │   ├── StudentController
    │   └── TransactionController
    │
    ├── service/             Business logic — author dedup, fine calc, student creation
    │   ├── BookService
    │   ├── StudentService
    │   └── TransactionService
    │
    ├── repository/          Raw JDBC — every SQL statement lives here
    │   ├── AuthorRepository
    │   ├── BookRepository
    │   ├── StudentRepository
    │   └── TransactionRepository
    │
    ├── model/               Plain Java objects — no JPA annotations
    │   ├── Student, Author, Book, Transaction, Address
    │   └── Enums: BookType, StudentStatus, TransactionType
    │
    ├── request/             Inbound DTOs — what the API accepts
    │   ├── BookCreationRequest
    │   ├── StudentCreationRequest
    │   └── BookTransactionRequest
    │
    ├── response/            Outbound DTOs — what the API returns
    │   ├── Response           (base class: status + message)
    │   ├── BookCreationResponse
    │   ├── StudentCreationResponse
    │   └── TransactionResponse
    │
    └── annotations/         Custom validation
        ├── ValidAge           (annotation definition)
        └── StudentAgeValidator (the actual logic: DOB must be ≥8 years ago)
```

---

## File-by-file walkthrough

| File | What it does |
|---|---|
| `BookController.java` | Accepts `POST /books/create/book`. Returns 201 on success, 400 if the book couldn't be saved. |
| `StudentController.java` | Accepts `POST /students/create/student`. The `@Valid` annotation triggers the custom age check before the method body runs. |
| `TransactionController.java` | Accepts `POST /transaction/book/initiate`. Routes ISSUE / RENEW / RETURN through a single method. |
| `BookService.java` | Tries to find the author first; creates them if not found. Then inserts the book. Two separate try-catch blocks handle the two possible failure points independently. |
| `StudentService.java` | Builds a `Student` object from the request, sets status to `ACTIVE`, delegates to the repository. Maps repository result to `StudentCreationResponse`. |
| `TransactionService.java` | Calls `TransactionRepository` to do the database work, then fetches the book record by ID to populate the transaction response with the book name. |
| `AuthorRepository.java` | Uses `queryForObject` with a `RowMapper` to look up an author. Throws if no match found — the service catches this to detect "author doesn't exist yet." |
| `BookRepository.java` | `createBookInDatabase` — straight INSERT. `findBookById` — SELECT by ID. Note: the SELECT query fetches only `BOOK_ID` but the `RowMapper` tries to read columns 1, 2, 3 — this would fail on a real SELECT * fix. |
| `StudentRepository.java` | Uses `SimpleJdbcInsert` with `usingGeneratedKeyColumns("id")` to get back the new student's ID, then immediately uses it to insert the address. |
| `TransactionRepository.java` | The most complex repository. Handles three SQL branches. The `calculateFine` method queries the original issue timestamp and computes days × 2 − amount_paid. |
| `ValidAge.java` | Custom annotation. Minimum age defaults to 8 but is configurable via the `age()` attribute. |
| `StudentAgeValidator.java` | Implements `ConstraintValidator<ValidAge, LocalDate>`. Rejects null-safe (null passes), future dates, and ages below the minimum. |
| `application.properties` | Points at a local MySQL instance (`jbdl8_library`), enables JDBC debug logging. |

---

## API reference

| Method | Path | Body | What it does |
|---|---|---|---|
| POST | `/books/create/book` | `BookCreationRequest` | Create a new book (and author if needed) |
| POST | `/students/create/student` | `StudentCreationRequest` | Register a new student |
| POST | `/transaction/book/initiate` | `BookTransactionRequest` | Issue, renew, or return a book |

### BookCreationRequest

```json
{
  "bookId": 1,
  "bookName": "Clean Code",
  "description": "A guide to writing readable software",
  "authorName": "Robert Martin",
  "authorEmail": "uncle.bob@example.com",
  "authorMobile": "9999999999",
  "bookType": "CODING",
  "bookPrice": 499.0,
  "publisher": "Prentice Hall"
}
```

### StudentCreationRequest

```json
{
  "name": "Riya Sharma",
  "email": "riya@example.com",
  "mobileNumber": "9876543210",
  "dob": "2005-04-15",
  "address": {
    "street": "12 MG Road",
    "city": "Bengaluru",
    "pincode": "560001"
  }
}
```

### BookTransactionRequest

```json
{
  "studentId": 3,
  "bookId": 7,
  "amount": "50",
  "requestType": "ISSUE"
}
```

`requestType` must be one of: `ISSUE`, `RENEW`, `RETURN`.

### Response shape (all endpoints)

```json
{
  "status": "CREATED",
  "message": "Book Created",
  "bookName": "Clean Code"
}
```

The `status` and `message` fields are always present. Additional fields depend on the endpoint.

---

## Data models

### Enums

**BookType** — `SCI_FI`, `COMIC`, `NOVEL`, `DRAMA`, `CODING`, `BIOGRAPHY`

**StudentStatus** — `ACTIVE`, `INACTIVE`, `DEACTIVE`

**TransactionType** — `ISSUED`, `RETURN`, `RENEW`

### Expected database schema (inferred from repository queries)

```sql
CREATE TABLE Student (
  id        INT AUTO_INCREMENT PRIMARY KEY,
  name      VARCHAR(255),
  email     VARCHAR(255),
  dob       DATE,
  mobile    VARCHAR(20),
  status    VARCHAR(20),
  createdOn DATETIME,
  updatedOn DATETIME
);

CREATE TABLE Address (
  studentId INT,
  street    VARCHAR(255),
  city      VARCHAR(255),
  pincode   VARCHAR(20)
);

CREATE TABLE Author (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  name         VARCHAR(255),
  email        VARCHAR(255),
  mobileNumber VARCHAR(20)
);

CREATE TABLE Book (
  id          INT PRIMARY KEY,
  name        VARCHAR(255),
  description TEXT,
  bookType    VARCHAR(50),
  bookPrice   DECIMAL(10,2),
  publisher   VARCHAR(255),
  STUDENT_ID  INT  -- nullable; set when issued, cleared on return
);

CREATE TABLE Transactions (
  TXN_ID       VARCHAR(36) PRIMARY KEY,
  STUDENT_ID   INT,
  BOOK_ID      INT,
  ISSUED_TIME  DATETIME,
  UPDATED_TIME DATETIME,
  COST         VARCHAR(50),
  TXN_TYPE     VARCHAR(20)
);
```

---

## Design decisions worth noting

**Raw JDBC over JPA.** Every query is written explicitly. There's no lazy loading, no N+1 risk hiding in an ORM, and the SQL is readable directly in the repository class. The trade-off is verbosity — `RowMapper` implementations repeat column names — but the data layer is transparent.

**Author deduplication via exception handling.** `queryForObject` throws when it finds no rows. The service catches this to mean "author doesn't exist, create them." This works but conflates "query failed" with "no results." In a production codebase, using `query()` and checking `list.isEmpty()` would be more explicit.

**`@Valid` at the controller boundary.** The `@ValidAge` check on `dob` fires before the service layer is ever called. Invalid ages get a 400 before any business logic runs. The same pattern is set up for `BookCreationRequest` but `@Valid` is not yet added to the `createBook` method signature.

**`SimpleJdbcInsert` for student creation.** The auto-increment `id` generated by MySQL comes back immediately via `executeAndReturnKey`. This lets us insert the student's address in the same request without a separate SELECT to find the new ID.

**Fine formula: `days × 2 − amount_paid`.** Each day a book is held beyond what was paid for costs ₹2. The amount the student paid at issue time is subtracted to give the balance owed on return. The fine is always computed fresh from the `ISSUED_TIME` in the database — not from anything the client sends.
