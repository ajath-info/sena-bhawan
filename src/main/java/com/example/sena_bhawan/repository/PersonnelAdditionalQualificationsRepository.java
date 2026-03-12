package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.PersonnelAdditionalQualifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonnelAdditionalQualificationsRepository extends JpaRepository<PersonnelAdditionalQualifications, Long> {
    @Query("SELECT DISTINCT qualification FROM PersonnelAdditionalQualifications")
    List<String> additionalQualificationList();
}
