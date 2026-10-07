-- ==========================================================
-- Hostel Management System - MySQL Database Schema
-- Database: hostel_management
-- ==========================================================

-- 1. Create the database if it doesn't already exist
CREATE DATABASE IF NOT EXISTS hostel_management
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- 2. Select the database
USE hostel_management;

-- 3. Create the 'students' table
CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    home_address TEXT NOT NULL,
    department_name VARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_student_account_number UNIQUE (account_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Sample Seed Data (Optional for testing)
-- INSERT INTO students (account_number, name, date_of_birth, home_address, department_name, created_at)
-- VALUES ('HOSTEL000001', 'Alex Rivera', '2003-08-15', '124 Campus Avenue, Block B', 'Computer Science & Engineering', NOW());
