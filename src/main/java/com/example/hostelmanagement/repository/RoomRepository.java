package com.example.hostelmanagement.repository;

import com.example.hostelmanagement.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByStatusAndHostel_Status(String status, String hostelStatus);
    List<Room> findAllByOrderByHostel_NameAscRoomNumberAsc();
    boolean existsByHostel_IdAndRoomNumberIgnoreCase(Long hostelId, String roomNumber);
    long countByStatus(String status);
}
