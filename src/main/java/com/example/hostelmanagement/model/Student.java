package com.example.hostelmanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "students", uniqueConstraints = {
    @UniqueConstraint(columnNames = "account_number", name = "uk_student_account_number"),
    @UniqueConstraint(columnNames = "student_id", name = "uk_student_id"),
    @UniqueConstraint(columnNames = "email", name = "uk_student_email")
})
@Getter
@Setter
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true, length = 30)
    private String accountNumber;

    @Column(name = "student_id", unique = true, length = 30)
    private String studentId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(length = 120)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 20)
    private String academicYear;

    @Column(length = 20)
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "home_address", nullable = false, columnDefinition = "TEXT")
    private String homeAddress;

    @Column(name = "department_name", nullable = false, length = 100)
    private String departmentName;

    @Column(length = 100)
    private String parentName;

    @Column(length = 30)
    private String parentPhone;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Student() {
    }

    public Student(String accountNumber, String name, LocalDate dateOfBirth, String homeAddress, String departmentName) {
        this.accountNumber = accountNumber;
        this.studentId = accountNumber;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.homeAddress = homeAddress;
        this.departmentName = departmentName;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public String getStudentId() { return studentId == null ? accountNumber : studentId; }

    // Helper methods for presentation
    public String getFormattedDob() {
        if (dateOfBirth == null) return "";
        return dateOfBirth.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public String getFormattedCreatedAt() {
        if (createdAt == null) return "";
        return createdAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a"));
    }
}
