package com.example.hostelmanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fees")
@Getter
@Setter
@NoArgsConstructor
public class Fee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;
    @Column(nullable = false)
    private LocalDate dueDate;
    @Column(nullable = false, length = 20)
    private String status = "PENDING";
    public BigDecimal getPendingAmount() { return amount.subtract(paidAmount).max(BigDecimal.ZERO); }
}
