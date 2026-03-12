package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.PersonnelAdditionalQualifications;
import com.example.sena_bhawan.entity.PersonnelSports;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonnelSportsRepository extends JpaRepository<PersonnelSports, Long> {
    @Query("SELECT DISTINCT sportName FROM PersonnelSports")
    List<String> additionalQualificationList();
}
