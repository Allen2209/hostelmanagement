package com.example.hostelmanagement.repository;

import com.example.hostelmanagement.model.MessPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface MessPreferenceRepository extends JpaRepository<MessPreference, Long> {
    Optional<MessPreference> findByStudent_Id(Long studentId);
    long countByFoodPreferenceIgnoreCase(String preference);
    List<MessPreference> findAllByOrderByIdAsc();
}
