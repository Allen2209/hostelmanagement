package com.example.hostelmanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "hostels")
@Getter
@Setter
@NoArgsConstructor
public class Hostel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(length = 200)
    private String location;
    @Column(length = 1000)
    private String description;
    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";
}
