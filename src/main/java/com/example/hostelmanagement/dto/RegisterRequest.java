package com.example.hostelmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Date of birth is required")
    private String dateOfBirth;

    @NotBlank(message = "Home address is required")
    @Size(min = 5, max = 500, message = "Home address must be between 5 and 500 characters")
    private String homeAddress;

    @NotBlank(message = "Department name is required")
    private String departmentName;

    @NotBlank(message = "Student ID is required")
    @Size(min = 2, max = 30, message = "Student ID must be 2 to 30 characters")
    private String studentId;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9+() .-]{7,20}$", message = "Enter a valid phone number")
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
    private String password;

    @NotBlank(message = "Academic year is required")
    private String academicYear;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Parent or guardian name is required")
    private String parentName;

    @NotBlank(message = "Parent or guardian phone is required")
    @Pattern(regexp = "^[0-9+() .-]{7,20}$", message = "Enter a valid parent phone number")
    private String parentPhone;

    public RegisterRequest() {
    }

    public RegisterRequest(String name, String dateOfBirth, String homeAddress, String departmentName) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.homeAddress = homeAddress;
        this.departmentName = departmentName;
        this.studentId = name == null ? null : name.replaceAll("[^A-Za-z0-9]", "");
        this.email = this.studentId == null ? null : this.studentId.toLowerCase() + "@example.test";
        this.phone = "0000000000";
        this.password = "Student123!";
        this.academicYear = "1";
        this.gender = "OTHER";
        this.parentName = "Parent";
        this.parentPhone = "0000000000";
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

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }
    public String getParentPhone() { return parentPhone; }
    public void setParentPhone(String parentPhone) { this.parentPhone = parentPhone; }
}
