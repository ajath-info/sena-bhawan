package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.PersonnelInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonnelInformationRepository
        extends JpaRepository<PersonnelInformation, Long> {

    List<PersonnelInformation> findByPersonnelId(Long personnelId);
}



