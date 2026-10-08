package com.example.hostelmanagement.repository;

import com.example.hostelmanagement.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByNameIgnoreCaseAndDateOfBirth(String name, LocalDate dateOfBirth);

    boolean existsByNameIgnoreCaseAndDateOfBirth(String name, LocalDate dateOfBirth);

    boolean existsByAccountNumber(String accountNumber);

    @Query("SELECT MAX(s.accountNumber) FROM Student s WHERE s.accountNumber LIKE 'HOSTEL%'")
    String findHighestAccountNumber();

    Optional<Student> findByAccountNumber(String accountNumber);

    Optional<Student> findByUserAccount_UsernameIgnoreCase(String username);

    Optional<Student> findByStudentIdIgnoreCase(String studentId);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByStudentIdIgnoreCase(String studentId);

    List<Student> findAllByOrderByNameAsc();

    long countByRoomIsNotNull();
    List<Student> findByRoom_Id(Long roomId);

    @Query("select s from Student s where lower(s.name) like lower(concat('%', :term, '%')) "
            + "or lower(s.studentId) like lower(concat('%', :term, '%')) "
            + "or lower(s.email) like lower(concat('%', :term, '%')) "
            + "or lower(s.departmentName) like lower(concat('%', :term, '%')) order by s.name")
    List<Student> search(@Param("term") String term);
}
