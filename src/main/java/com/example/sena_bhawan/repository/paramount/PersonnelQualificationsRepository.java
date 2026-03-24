package com.example.sena_bhawan.repository.paramount;

import com.example.sena_bhawan.entity.PersonnelQualifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PersonnelQualificationsRepository extends JpaRepository<PersonnelQualifications, Long> {
    List<PersonnelQualifications> findByPersonnelId(Long personnelId);
}
