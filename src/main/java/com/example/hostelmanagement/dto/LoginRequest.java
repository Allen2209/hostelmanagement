package com.example.hostelmanagement.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Please enter your name")
    private String name;

    @NotBlank(message = "Please enter your Date of Birth (format: DD-MM-YYYY)")
    private String dateOfBirth;

    public LoginRequest() {
    }

    public LoginRequest(String name, String dateOfBirth) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
