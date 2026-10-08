package com.example.hostelmanagement.repository;

import com.example.hostelmanagement.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByStudent_IdOrderByCreatedAtDesc(Long studentId);
    List<LeaveRequest> findAllByOrderByCreatedAtDesc();
    long countByStatus(String status);
}
