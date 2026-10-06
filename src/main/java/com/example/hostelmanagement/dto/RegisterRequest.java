package com.example.hostelmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Date of Birth is required (format: DD-MM-YYYY)")
    private String dateOfBirth;

    @NotBlank(message = "Home address is required")
    @Size(min = 5, max = 500, message = "Home address must be between 5 and 500 characters")
    private String homeAddress;

    @NotBlank(message = "Department name is required")
    private String departmentName;

    public RegisterRequest() {
    }

    public RegisterRequest(String name, String dateOfBirth, String homeAddress, String departmentName) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.homeAddress = homeAddress;
        this.departmentName = departmentName;
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

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
