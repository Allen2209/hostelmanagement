package com.example.hostelmanagement.repository;

import com.example.hostelmanagement.model.RoomRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomRequestRepository extends JpaRepository<RoomRequest, Long> {
    List<RoomRequest> findByStudent_IdOrderByRequestDateDesc(Long studentId);
    List<RoomRequest> findAllByOrderByRequestDateDesc();
    long countByStatus(String status);
    boolean existsByStudent_IdAndRoom_IdAndStatus(Long studentId, Long roomId, String status);
}
