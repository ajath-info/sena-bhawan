package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.CourseMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface CourseMasterRepository extends JpaRepository<CourseMaster, Integer> {

    List<CourseMaster> findBySrnoIn(Set<Integer> srnos);
}
