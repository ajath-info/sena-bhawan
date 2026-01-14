package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.CourseDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseDetailsRepository extends JpaRepository<CourseDetails, Long> {
    List<CourseDetails> findByPersonnelId(Long personnelId);
}
