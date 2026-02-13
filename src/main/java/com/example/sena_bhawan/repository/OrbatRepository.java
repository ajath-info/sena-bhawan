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

    // ================= ORBAT NAME DROPDOWNS =================
    @Query("select distinct o.commandName from OrbatStructure o where o.commandName is not null")
    List<String> findDistinctCommandNames();

    @Query("select distinct o.corpsName from OrbatStructure o where o.corpsName is not null")
    List<String> findDistinctCorpsNames();

    @Query("select distinct o.divisionName from OrbatStructure o where o.divisionName is not null")
    List<String> findDistinctDivisionNames();

    // ================= FILTER DROPDOWNS =================
    @Query("select distinct o.rank from OrbatStructure o where o.rank is not null")
    List<String> findDistinctRanks();

    @Query("select distinct o.medicalCategory from OrbatStructure o where o.medicalCategory is not null")
    List<String> findDistinctMedicalCategories();

    @Query("select distinct o.establishmentType from OrbatStructure o where o.establishmentType is not null")
    List<String> findDistinctEstablishmentTypes();

    @Query("select distinct o.areaType from OrbatStructure o where o.areaType is not null")
    List<String> findDistinctAreaTypes();

    @Query("select distinct o.civilQualification from OrbatStructure o where o.civilQualification is not null")
    List<String> findDistinctCivilQualifications();

    @Query("select distinct o.sports from OrbatStructure o where o.sports is not null")
    List<String> findDistinctSports();


    @Query("select distinct o.postingDueMonths from OrbatStructure o where o.postingDueMonths is not null")
    List<Integer> findDistinctPostingDueMonths();
}
