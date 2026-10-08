package com.example.hostelmanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    @Column(nullable = false, length = 150)
    private String title;
    @Column(nullable = false, length = 1000)
    private String message;
    @Column(nullable = false)
    private boolean isRead;
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
