package com.example.sena_bhawan.repository.paramount;

import com.example.sena_bhawan.entity.PersonnelMedicalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PersonnelMedicalDetailsRepository extends JpaRepository<PersonnelMedicalDetails, Long> {
    List<PersonnelMedicalDetails> findByPersonnelId(Long personnelId);
}