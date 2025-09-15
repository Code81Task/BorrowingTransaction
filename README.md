# BorrowingTransaction

## Overview
This is a Spring Boot-based application for managing borrowing transactions in a library system. It handles book borrowing, returning, overdue tracking, and fee calculations. The system uses JPA for database interactions, Flyway for database migrations, and Docker for containerized deployment, ensuring data consistency and scalability.

## Key Features
- **Borrowing and Returning Books**: Create borrowing transactions with due dates and return books with automatic overdue fee calculation.
- **Transaction Management**: CRUD operations for borrowing transactions with filtering by member or book.
- **Overdue Handling**: Automatic marking of overdue transactions with fee computation via a scheduled task.
- **Data Consistency**: Prevents race conditions during borrowing to ensure a book cannot be borrowed by multiple members simultaneously.
- **Database Migrations**: Uses Flyway to manage database schema versioning and migrations.
- **Containerization**: Deployable using Docker and Docker Compose for consistent environments.

## Implementation Highlights

### 1. Race Condition Handling
To prevent race conditions when multiple users attempt to borrow the same book simultaneously, the `createTransaction` method uses:
- **Pessimistic Locking**: The `findActiveByBookIdForUpdate` repository method applies `@Lock(LockModeType.PESSIMISTIC_WRITE)` to lock relevant transaction records during the check.
- **Serializable Isolation Level**: The `@Transactional(isolation = Isolation.SERIALIZABLE)` annotation ensures transactions execute sequentially, avoiding concurrent modifications.

This guarantees that only one borrowing request succeeds if the book is unavailable, maintaining data integrity.

### 2. Scheduler for Overdue Transactions
An automated scheduler runs daily to identify and update overdue borrowings:
- **OverdueScheduler Component**: Uses `@Scheduled(cron = "0 5 0 * * *")` to execute at 12:05 AM daily.
- **markOverdueTransactions Method**: Queries active "Borrowed" transactions past their due date, updates status to "Overdue", calculates overdue days and fees (based on configurable `feePerDay`).

This ensures timely updates without manual intervention.

### 3. Pagination Support
Efficient handling of large datasets in transaction retrieval:
- **getTransactions Method**: Supports optional filters (by `memberId`, `bookId`, or both) and uses `Pageable` for pagination.
- **Repository Methods**: Custom queries like `findByMemberId`, `findByBookId`, and `findByMemberIdAndBookId` return `Page<BorrowingTransaction>` for paginated results.

This improves performance and user experience when listing transactions.

### 4. Database Migrations with Flyway
The application uses **Flyway** to manage database schema versioning and migrations:
- Flyway scripts (located in `src/main/resources/db/migration`) automatically create and update the database schema.
- Ensures consistent database setup across environments (development, testing, production).

## Prerequisites
- **Docker** and **Docker Compose** installed.
- **Java 17** or later (for local development without Docker).
- **Maven** (for building the project locally).

## Setup and Running
1. Clone the repository:
   ```bash
   git clone https://github.com/Code81Task/BorrowingTransaction.git
   ```
2. Navigate to the project directory:
   ```bash
   cd BorrowingTransaction
   ```
3. Configure database properties in `application.properties` or use environment variables for Docker (e.g., database URL, username, password).
4. Run the application using Docker Compose:
   ```bash
   docker-compose up -d
   ```
    - This starts the application and database in detached mode.
    - Flyway will automatically apply database migrations during startup.
5. Alternatively, for local development without Docker:
    - Build the project: `mvn clean install`
    - Run: `mvn spring-boot:run`
6. to show document for endpoints:
   ```
    http://localhost:808/swagger-ui/index.html#/
   ```
## Docker Compose Configuration
The `docker-compose.yml` file includes services for:
- **Application**: The Spring Boot application.
- **Database**: A relational database (e.g., MySQL or PostgreSQL) configured for the app.
  Ensure the database configuration in `application.properties` or environment variables matches the Docker Compose setup.

## Dependencies
- Spring Boot 3.x
- Spring Data JPA
- Flyway (for database migrations)
- H2 Database (for testing) or MySQL/PostgreSQL (for production)
- Docker and Docker Compose

## Future Enhancements
- Add validation for member/book existence before creating transactions.
- Integrate email notifications for overdue transactions.
- Add unit and integration tests for better reliability.
- Support for advanced filtering in transaction retrieval.

For more details, refer to the source code in `src/main/java`.