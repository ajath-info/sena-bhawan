package com.example.sena_bhawan.repository.paramount;


import com.example.sena_bhawan.entity.PersonnelFamily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PersonnelFamilyRepository extends JpaRepository<PersonnelFamily, Long> {
    List<PersonnelFamily> findByPersonnelId(Long personnelId);
}
