package com.example.hostelmanagement.repository;

import com.example.hostelmanagement.model.Fee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeeRepository extends JpaRepository<Fee, Long> {
    List<Fee> findByStudent_IdOrderByDueDateDesc(Long studentId);
    List<Fee> findAllByOrderByDueDateDesc();
    long countByStatusIn(Iterable<String> statuses);
}
