package com.example.sena_bhawan.repository.paramount;

import com.example.sena_bhawan.entity.PersonnelDecorations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PersonnelDecorationsRepository extends JpaRepository<PersonnelDecorations, Long> {
    List<PersonnelDecorations> findByPersonnelIdOrderByAwardDateDesc(Long personnelId);
}
