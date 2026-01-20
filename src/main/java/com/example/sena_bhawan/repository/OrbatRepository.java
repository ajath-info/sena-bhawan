package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.OrbatStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface OrbatRepository extends JpaRepository<OrbatStructure, Long> {

    List<OrbatStructure> findByFormationType(String type);

    List<OrbatStructure> findByParentId(Long parentId);

    @Query("SELECT DISTINCT o.sosNo FROM OrbatStructure o WHERE o.sosNo IS NOT NULL AND o.sosNo <> ''")
    List<String> findDistinctSosNumbers();

    @Query("SELECT o FROM OrbatStructure o WHERE o.sosNo IS NOT NULL AND o.sosNo <> '' ORDER BY o.name")
    List<OrbatStructure> findAllWithSosNumbers();
}
