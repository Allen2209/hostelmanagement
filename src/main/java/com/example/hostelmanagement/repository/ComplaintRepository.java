package com.example.hostelmanagement.repository;

import com.example.hostelmanagement.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByStudent_IdOrderByCreatedAtDesc(Long studentId);
    List<Complaint> findAllByOrderByCreatedAtDesc();
    long countByStatusIn(Iterable<String> statuses);
}
