package com.example.hostelmanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_requests")
@Getter
@Setter
@NoArgsConstructor
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    @Column(nullable = false)
    private LocalDate fromDate;
    @Column(nullable = false)
    private LocalDate toDate;
    @Column(nullable = false, length = 1000)
    private String reason;
    @Column(nullable = false, length = 100)
    private String parentName;
    @Column(nullable = false, length = 30)
    private String parentPhone;
    @Column(nullable = false, length = 20)
    private String status = "PENDING";
    @Column(length = 500)
    private String adminRemarks;
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
