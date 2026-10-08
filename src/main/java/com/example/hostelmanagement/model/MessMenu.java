package com.example.hostelmanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "mess_menus")
@Getter
@Setter
@NoArgsConstructor
public class MessMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDate menuDate;
    @Column(nullable = false, length = 1000)
    private String breakfast;
    @Column(nullable = false, length = 1000)
    private String lunch;
    @Column(nullable = false, length = 1000)
    private String snacks;
    @Column(nullable = false, length = 1000)
    private String dinner;
}
