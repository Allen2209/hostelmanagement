package com.example.hostelmanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rooms", uniqueConstraints = @UniqueConstraint(name = "uk_hostel_room", columnNames = {"hostel_id", "room_number"}))
@Getter
@Setter
@NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "hostel_id", nullable = false)
    private Hostel hostel;
    @Column(name = "room_number", nullable = false, length = 30)
    private String roomNumber;
    @Column(nullable = false)
    private int capacity;
    @Column(nullable = false)
    private int occupied;
    @Column(name = "room_type", length = 40)
    private String roomType;
    @Column(nullable = false, length = 20)
    private String status = "AVAILABLE";
    public int getAvailableBeds() { return Math.max(0, capacity - occupied); }
}
