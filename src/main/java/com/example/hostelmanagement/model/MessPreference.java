package com.example.hostelmanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mess_preferences", uniqueConstraints = @UniqueConstraint(name = "uk_mess_preference_student", columnNames = "student_id"))
@Getter
@Setter
@NoArgsConstructor
public class MessPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;
    @Column(nullable = false, length = 30)
    private String foodPreference;
    private boolean breakfast;
    private boolean lunch;
    private boolean snacks;
    private boolean dinner;
}
