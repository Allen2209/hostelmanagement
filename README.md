# 🏢 Hostel Management System

A clean, modern, and robust **Hostel Management Web Application** designed for college hostel residents and administrative record keeping. Built with **Java 21**, **Spring Boot 3**, **Spring Data JPA / Hibernate**, **MySQL**, **Thymeleaf**, and modern responsive CSS/JavaScript.

---

## 📑 Table of Contents

1. [Key Features](#-key-features)
2. [Technology Stack](#-technology-stack)
3. [Project Structure](#-project-structure)
4. [Prerequisites & Software Setup](#-prerequisites--software-setup)
   - [1. Java 21 Installation](#1-java-21-installation)
   - [2. Apache Maven Installation](#2-apache-maven-installation)
   - [3. MySQL Installation & Database Setup](#3-mysql-installation--database-setup)
5. [Configuring MySQL Credentials](#-configuring-mysql-credentials)
6. [Building & Running the Application](#-building--running-the-application)
   - [Option A: Run using Maven command](#option-a-run-using-maven-command)
   - [Option B: Run as a Packaged JAR](#option-b-run-as-a-packaged-jar)
   - [Option C: Quick Start via `run.bat` (Windows)](#option-c-quick-start-via-runbat-windows)
7. [Application Flow & Usage Guide](#-application-flow--usage-guide)
   - [Accessing the Portal](#accessing-the-portal)
   - [Registering a New Resident](#registering-a-new-resident)
   - [Logging In](#logging-in)
   - [Viewing the Dashboard & Logging Out](#viewing-the-dashboard--logging-out)
8. [Database Schema Details](#-database-schema-details)
9. [Troubleshooting & FAQs](#-troubleshooting--faqs)

---

## ✨ Key Features

- **Automated Account Number Generation**: Unique, sequential hostel account numbers (`HOSTEL000001`, `HOSTEL000002`, ...) automatically generated and assigned upon student registration.
- **Resident Authentication**: Streamlined student login using:
  - **Username:** Resident's Full Name
  - **Password:** Date of Birth in `DD-MM-YYYY` format
- **Resident Registration**: Self-service onboarding capturing name, date of birth, home address, and department/branch with backend validation.
- **Duplicate Prevention**: Intelligently prevents duplicate registrations for the same student name and date of birth.
- **Hostel Dashboard**: Clean resident portal showing verified hostel account number, personal details, department, registration timestamp, and session logout.
- **Modern Responsive UI**: Styled with clean CSS cards, SVG icons, mobile-friendly grid, date format mask helper, calendar picker sync, and copy-to-clipboard functionality.
- **Graceful Error Handling**: User-friendly alerts and error pages preventing raw stack traces.

---

## 🛠 Technology Stack

- **Backend:** Java 21 LTS, Spring Boot 3.3.4
- **Web Layer:** Spring Web MVC
- **Templates:** Thymeleaf (Server-Side HTML Rendering)
- **Persistence:** Spring Data JPA, Hibernate ORM
- **Database:** MySQL 8.0+
- **Frontend:** HTML5, CSS3, Modern JavaScript (Vanilla JS, no heavy frameworks)
- **Build Tool:** Apache Maven 3.9+

---

## 📂 Project Structure

```text
hostel-management/
├── pom.xml                                 # Maven project descriptor & dependencies
├── README.md                               # Comprehensive setup and usage manual
├── schema.sql                              # Standalone MySQL schema definition & seed
├── run.bat                                 # Windows one-click launcher
│
└── src/
    ├── main/
    │   ├── java/com/example/hostelmanagement/
    │   │   ├── HostelManagementApplication.java # Spring Boot entry point
    │   │   ├── controller/
    │   │   │   ├── AuthController.java          # /login, /register, /logout
    │   │   │   └── DashboardController.java     # /dashboard
    │   │   ├── dto/
    │   │   │   ├── LoginRequest.java            # Form validation for login
    │   │   │   └── RegisterRequest.java         # Form validation for registration
    │   │   ├── exception/
    │   │   │   ├── GlobalExceptionHandler.java  # User-friendly error page
    │   │   │   └── StudentAlreadyExistsException.java
    │   │   ├── model/
    │   │   │   └── Student.java                 # JPA Entity (students table)
    │   │   ├── repository/
    │   │   │   └── StudentRepository.java       # Spring Data JPA repository
    │   │   └── service/
    │   │       └── StudentService.java          # Account generation & business logic
    │   │
    │   └── resources/
    │       ├── application.properties           # MySQL connection and server config
    │       ├── static/
    │       │   ├── css/
    │       │   │   └── style.css                # Custom modern styling
    │       │   └── js/
    │       │       └── script.js                # Mask, calendar sync & copy helper
    │       └── templates/
    │           ├── login.html                   # Login page
    │           ├── register.html                # Registration page with success view
    │           ├── dashboard.html               # Resident dashboard
    │           └── error.html                   # Custom error template
    │
    └── test/
        └── java/com/example/hostelmanagement/
            └── service/
                └── StudentServiceTest.java      # Unit tests for account generation & registration
```

---

## 📦 Prerequisites & Software Setup

Ensure you have the following software installed on your machine.

### 1. Java 21 Installation

1. Download Java 21 JDK from [Oracle Java Downloads](https://www.oracle.com/java/technologies/downloads/#java21) or [Eclipse Temurin](https://adoptium.net/temurin/releases/?version=21).
2. Run the installer and follow on-screen instructions.
3. Verify your Java installation:
   ```powershell
   java -version
   ```
   *Expected output:* `java version "21.x.x"`

4. Set `JAVA_HOME` environment variable (if not set automatically):
   - Open Windows **Settings** > **System** > **About** > **Advanced system settings** > **Environment Variables**.
   - Under *System variables*, add or edit `JAVA_HOME` pointing to your JDK folder (e.g. `C:\Program Files\Java\jdk-21`).
   - Add `%JAVA_HOME%\bin` to your `Path` variable.

---

### 2. Apache Maven Installation

1. Download Maven binary zip (e.g., `apache-maven-3.10.0-bin.zip`) from [maven.apache.org](https://maven.apache.org/download.cgi).
2. Extract the archive (e.g., to `C:\Program Files\apache-maven` or `C:\Users\<USER>\Documents\apache-maven-3.10.0`).
3. Add the `bin` directory of Maven to your `Path` environment variable:
   - Example: `C:\Users\AMAR\Documents\apache-maven-3.10.0\bin`
4. Open a new terminal and verify:
   ```powershell
   mvn -version
   ```
   *Expected output:* `Apache Maven 3.x.x ... Java version: 21`

---

### 3. MySQL Installation & Database Setup

1. Download and install **MySQL Community Server** from [MySQL Downloads](https://dev.mysql.com/downloads/installer/).
2. During setup:
   - Select default port **3306**.
   - Set a root password (e.g., `root`, `password123`, or your chosen password). Remember this password.
   - Keep the MySQL Windows Service running (`MySQL80`).
3. Open **MySQL Command Line Client** or **MySQL Workbench** and log in with your root password.
4. Create the application database:
   ```sql
   CREATE DATABASE IF NOT EXISTS hostel_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
5. *(Optional)* You can run the commands in `schema.sql` to inspect or create the table upfront, though **Spring Boot will automatically create and update the `students` table** upon application startup via Hibernate `ddl-auto=update`.

---

## ⚙ Configuring MySQL Credentials

Before running the application, enter your MySQL root password in the configuration file:

1. Open `src/main/resources/application.properties` in your text editor:
   ```properties
   # Location: hostel-management/src/main/resources/application.properties

   spring.datasource.url=jdbc:mysql://localhost:3306/hostel_management?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=YOUR_PASSWORD
   ```
2. Replace `YOUR_PASSWORD` with your actual MySQL password.
3. Save the file.

---

## 🚀 Building & Running the Application

Navigate to the `hostel-management` project directory:

```powershell
cd C:\Users\AMAR\projects\hack1\hostel-management
```

### Option A: Run using Maven command

```powershell
mvn spring-boot:run
```

*(If Maven is not in your global PATH, invoke the direct path):*
```powershell
& "C:\Users\AMAR\Documents\apache-maven-3.10.0\bin\mvn.cmd" spring-boot:run
```

### Option B: Run as a Packaged JAR

1. Package the project into an executable JAR:
   ```powershell
   mvn clean package
   ```
2. Run the generated JAR:
   ```powershell
   java -jar target/hostel-management-0.0.1-SNAPSHOT.jar
   ```

### Option C: Quick Start via `run.bat` (Windows)

Double-click `run.bat` inside the `hostel-management` folder or execute:
```powershell
.\run.bat
```

When started successfully, you will see Spring Boot console output:
```text
Tomcat started on port 8080 (http) with context path '/'
Started HostelManagementApplication in ... seconds
```

---

## 🌐 Application Flow & Usage Guide

### Accessing the Portal

Open your web browser and navigate to:

👉 **[http://localhost:8080](http://localhost:8080)**

You will automatically be routed to the **Login Page** (`http://localhost:8080/login`).

---

### Registering a New Resident

1. Click **"Register here"** or navigate to `http://localhost:8080/register`.
2. Fill out the registration form:
   - **Full Name**: e.g., `Alex Rivera`
   - **Date of Birth**: e.g., `15-08-2003` (enter in `DD-MM-YYYY` format or click the calendar icon to select)
   - **Department / Branch**: e.g., `Computer Science & Engineering`
   - **Permanent Home Address**: e.g., `124 Campus Avenue, Block B, City Center`
   - *Note:* The **Account Number** field is disabled and marked as auto-generated.
3. Click **"Register Resident"**.
4. **Registration Confirmation:**
   - The system validates the inputs and checks for duplicates.
   - Generates a unique account number (e.g. `HOSTEL000001`).
   - Displays a success card with the assigned Account Number, a **"Copy Account Number"** button, and login instructions.
5. Click **"Proceed to Login"**.

---

### Logging In

1. On the **Login Page** (`http://localhost:8080/login`):
   - **Full Name (Username)**: Enter your registered name (e.g. `Alex Rivera`)
   - **Date of Birth (Password)**: Enter your registered Date of Birth (e.g. `15-08-2003`)
2. Click **"Log In to Dashboard"**.
3. If credentials match a resident in MySQL, you will be redirected to `/dashboard`.
4. If credentials do not match, a clear error alert `Invalid Name or Date of Birth. Please check your credentials or register.` is displayed.

---

### Viewing the Dashboard & Logging Out

On the **Hostel Dashboard** (`http://localhost:8080/dashboard`):
- View your **Verified Hostel Account Number** (e.g., `HOSTEL000001`).
- View your **Full Name**, **Date of Birth**, **Department**, and **Home Address**.
- View your account **Registration Timestamp** and **Active Resident** status.
- Click **"Logout"** in the top navigation or profile card to securely end your session and return to the login screen.

---

## 🗄 Database Schema Details

The application creates/maintains the `students` table in the `hostel_management` database:

| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | PRIMARY KEY, AUTO_INCREMENT | Internal primary key |
| `account_number` | `VARCHAR(30)` | UNIQUE, NOT NULL | Automatically generated (`HOSTEL000001`) |
| `name` | `VARCHAR(100)` | NOT NULL | Resident student full name |
| `date_of_birth` | `DATE` | NOT NULL | Date of Birth (used as password) |
| `home_address` | `TEXT` | NOT NULL | Resident's permanent address |
| `department_name`| `VARCHAR(100)` | NOT NULL | Academic branch / department |
| `created_at` | `DATETIME` | NOT NULL | Onboarding timestamp |

---

## ❓ Troubleshooting & FAQs

### Q1: `Communications link failure` or `Access denied for user 'root'@'localhost'`
- Verify MySQL service is running in Windows Services (`services.msc`).
- Double-check the password set in `src/main/resources/application.properties`.

### Q2: `mvn is not recognized as an internal or external command`
- Either add `C:\Users\AMAR\Documents\apache-maven-3.10.0\bin` to your Windows `Path` variable, or run `run.bat` directly which detects the unzipped Maven automatically.

### Q3: How to test without MySQL or reset the database?
- To reset student records, log in to MySQL and run:
  ```sql
  USE hostel_management;
  TRUNCATE TABLE students;
  ```
- Subsequent registrations will automatically start generating from `HOSTEL000001` again.
