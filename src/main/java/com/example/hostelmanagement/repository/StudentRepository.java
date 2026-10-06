package com.example.hostelmanagement.repository;

import com.example.hostelmanagement.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByNameIgnoreCaseAndDateOfBirth(String name, LocalDate dateOfBirth);

    boolean existsByNameIgnoreCaseAndDateOfBirth(String name, LocalDate dateOfBirth);

    boolean existsByAccountNumber(String accountNumber);

    @Query("SELECT MAX(s.accountNumber) FROM Student s WHERE s.accountNumber LIKE 'HOSTEL%'")
    String findHighestAccountNumber();

    Optional<Student> findByAccountNumber(String accountNumber);
}
