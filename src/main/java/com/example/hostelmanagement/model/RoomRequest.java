package com.example.hostelmanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_requests")
@Getter
@Setter
@NoArgsConstructor
public class RoomRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
    @Column(length = 500)
    private String reason;
    @Column(nullable = false, length = 20)
    private String status = "PENDING";
    @Column(nullable = false)
    private LocalDateTime requestDate = LocalDateTime.now();
    @Column(length = 500)
    private String adminRemarks;
}
