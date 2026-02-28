package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.OrbatStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrbatRepository extends JpaRepository<OrbatStructure, Long> {

    Optional<OrbatStructure> findById(Long id);

    List<OrbatStructure> findByFormationType(String formationType);

//    List<OrbatStructure> findByFormationType(String type);

    List<OrbatStructure> findByParentId(Long parentId);

    @Query("SELECT DISTINCT o.susNo FROM OrbatStructure o WHERE o.susNo IS NOT NULL AND o.susNo <> ''")
    List<String> findDistinctSusNumbers();

    @Query("SELECT o FROM OrbatStructure o WHERE o.susNo IS NOT NULL AND o.susNo <> '' ORDER BY o.name")
    List<OrbatStructure> findAllWithSusNumbers();
}
