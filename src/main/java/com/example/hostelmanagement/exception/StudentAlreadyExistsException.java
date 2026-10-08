package com.example.hostelmanagement.exception;

public class StudentAlreadyExistsException extends RuntimeException {
    private final String existingAccountNumber;

    public StudentAlreadyExistsException(String message, String existingAccountNumber) {
        super(message);
        this.existingAccountNumber = existingAccountNumber;
    }

    public String getExistingAccountNumber() {
        return existingAccountNumber;
    }
}
