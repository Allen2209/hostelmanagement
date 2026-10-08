package com.example.hostelmanagement.repository;

import com.example.hostelmanagement.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByStudent_IdOrderByCreatedAtDesc(Long studentId);
}
