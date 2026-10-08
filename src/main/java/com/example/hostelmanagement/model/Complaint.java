package com.example.hostelmanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
@Getter
@Setter
@NoArgsConstructor
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    @Column(nullable = false, length = 40)
    private String category;
    @Column(nullable = false, length = 150)
    private String subject;
    @Column(nullable = false, length = 2000)
    private String description;
    @Column(nullable = false, length = 20)
    private String status = "PENDING";
    @Column(length = 1000)
    private String adminRemarks;
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
