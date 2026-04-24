package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.CourseMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface CourseMasterRepository extends JpaRepository<CourseMaster, Integer> {

    @Query("SELECT DISTINCT c FROM CourseMaster c LEFT JOIN FETCH c.schedules s WHERE s.year = :currentYear")
    List<CourseMaster> findAllWithSchedulesForCurrentYear(@Param("currentYear") String currentYear);


    List<CourseMaster> findBySrnoIn(Set<Integer> srnos);
}
