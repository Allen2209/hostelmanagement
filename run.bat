@echo off
REM ========================================================
REM Hostel Management System - Quick Start Launcher
REM ========================================================

echo ========================================================
echo  Starting Hostel Management System (Spring Boot)
echo ========================================================

REM Check if mvn is in PATH, otherwise use the detected Maven folder
where mvn >nul 2>nul
if %ERRORLEVEL% equ 0 (
    set MVN_CMD=mvn
) else if exist "C:\Users\AMAR\Documents\apache-maven-3.10.0\bin\mvn.cmd" (
    set MVN_CMD="C:\Users\AMAR\Documents\apache-maven-3.10.0\bin\mvn.cmd"
) else (
    echo [ERROR] Maven not found! Please ensure Apache Maven is installed or added to PATH.
    pause
    exit /b 1
)

echo Using Maven: %MVN_CMD%
echo.
%MVN_CMD% spring-boot:run
pause
