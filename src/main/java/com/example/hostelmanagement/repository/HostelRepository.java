package com.example.hostelmanagement.repository;

import com.example.hostelmanagement.model.Hostel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HostelRepository extends JpaRepository<Hostel, Long> { }
