package com.example.hostelmanagement.repository;

import com.example.hostelmanagement.model.MessMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessMenuRepository extends JpaRepository<MessMenu, Long> {
    List<MessMenu> findAllByOrderByMenuDateAsc();
}
