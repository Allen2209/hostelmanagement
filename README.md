# Hostel Management System

A simple Spring Boot and Thymeleaf college hostel portal with student and administrator accounts. Student records, room inventory, room requests, complaints, leave requests, mess preferences, menus, fees, and notifications are persisted with MySQL.

## Features

### Student
- Register and sign in using a student ID and password.
- View and update profile details; change password.
- Browse rooms, submit a room request, and view allocation/request status.
- Save mess preference and read published menus.
- Submit complaints and leave requests and track admin decisions.
- View fee balances and notifications.

### Admin
- Review student directory and deactivate accounts.
- Create/edit/deactivate hostels and rooms.
- Approve/reject room requests; approval rechecks room capacity and assigns the room transactionally.
- Update complaint and leave request statuses with remarks.
- Publish/delete mess menus and view meal preferences.
- Add fee records, record payments, and send student announcements.

The application inserts a small BCrypt-protected demo dataset on first startup. Records are not reinserted after the first run.

## Technology
- Java 21, Spring Boot 4.1.1, Maven
- Spring MVC, Spring Security, BCrypt, Spring Data JPA / Hibernate
- MySQL 8
- Thymeleaf, HTML, CSS, JavaScript

## Requirements and database setup

Install Java 21, Maven 3.9+, and MySQL 8. Create the database (or let the configured MySQL URL create it):

```sql
CREATE DATABASE hostel_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Configure the database with environment variables before starting the app:

PowerShell:

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-local-mysql-password"
mvn spring-boot:run
```

Alternatively, set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`. The default URL points to `localhost:3306/hostel_management`. Run the single root-level `schema.sql` file if you need to create the database manually. Hibernate creates/updates all tables at startup, and the app seeds demo data automatically.

## Run and test

```powershell
mvn clean
mvn test
mvn clean install
mvn spring-boot:run
```

Open `http://localhost:8080`.

## Development/demo accounts

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| Student | `student` | `student123` |
| Student | `student2` | `student123` |

These are local demonstration credentials. Change them before any non-demo use.

New student registration requires a unique student ID and email. The student ID is also used as the login username. Passwords are stored using BCrypt. Room and student pages are restricted by role; student data lookups use the authenticated account, not a user-supplied student ID.

## Main project structure

```text
src/main/java/com/example/hostelmanagement/
  config/       Security rules and idempotent demo data setup
  controller/   Authentication, student and admin page routes
  dto/          Validated form objects
  model/        JPA entities
  repository/   Spring Data repositories
  service/      Student and hostel workflow logic
src/main/resources/
  templates/    Thymeleaf student/admin pages and shared sidebar
  static/       Responsive CSS and JavaScript
```

## Notes / common issues
- `Communications link failure`: make sure the MySQL service is running and the database credentials/port are correct.
- `Access denied for user`: set `DB_USERNAME` and `DB_PASSWORD` for your local MySQL account.
- Ensure MySQL can create databases when relying on `createDatabaseIfNotExist=true`; otherwise create `hostel_management` manually.
- Student and admin page access is enforced with Spring Security. Sign in with the appropriate account to reach the relevant sidebar.
- Payments are recorded by an administrator; no online payment gateway is included.

## Testing

`mvn test` runs the existing registration/account tests, portal template rendering checks, and room approval/request service tests.
