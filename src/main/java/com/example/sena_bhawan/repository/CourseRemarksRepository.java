package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.CourseRemarks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRemarksRepository
        extends JpaRepository<CourseRemarks, Long> {

    List<CourseRemarks> findByPersonnelId(Long personnelId);
}
