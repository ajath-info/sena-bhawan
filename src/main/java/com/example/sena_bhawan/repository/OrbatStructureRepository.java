package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.OrbatStructure;
import com.example.sena_bhawan.dto.OrbatSimpleDTO;
import com.example.sena_bhawan.projection.OrbatStructureProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrbatStructureRepository extends JpaRepository<OrbatStructure, Long> {
    @Query("SELECT DISTINCT o.name FROM OrbatStructure o WHERE LOWER(o.formationType) = 'unit'")
    List<String> findAllUnitNames();

    @Query("SELECT DISTINCT o.name FROM OrbatStructure o WHERE LOWER(o.formationType) = 'corps'")
    List<String> findAllCorpsNames();

    @Query("SELECT DISTINCT o.name FROM OrbatStructure o WHERE LOWER(o.formationType) = 'command'")
    List<String> findAllCommandNames();

    @Query("SELECT DISTINCT areaType FROM OrbatStructure")
    List<String> getAllAreaType();

    List<OrbatStructure> findByIdInAndFormationTypeIn(List<Long> orbatIds, List<String> formationTypes);
    @Query("SELECT o.id as id, o.name as name, o.formationType as formationType FROM OrbatStructure o WHERE o.id IN :orbatIds AND o.formationType IN :formationTypes")
    List<OrbatStructureProjection> findIdAndNameByIdInAndFormationTypeIn(@Param("orbatIds") List<Long> orbatIds, @Param("formationTypes") List<String> formationTypes);
    Optional<OrbatStructure> findById(Long id);

    List<OrbatStructure> findByFormationType(String formationType);

    // 1️⃣ All Commands
    @Query("""
           SELECT new com.example.sena_bhawan.dto.OrbatSimpleDTO(o.id, o.name)
           FROM OrbatStructure o
           WHERE o.formationType = 'command'
           ORDER BY o.name
           """)
    List<OrbatSimpleDTO> findAllCommands();

    // 2️⃣ All Corps
    @Query("""
           SELECT new com.example.sena_bhawan.dto.OrbatSimpleDTO(o.id, o.name)
           FROM OrbatStructure o
           WHERE o.formationType = 'corps'
           ORDER BY o.name
           """)
    List<OrbatSimpleDTO> findAllCorps();


    // 3️⃣ Corps by Command
    @Query("""
       SELECT new com.example.sena_bhawan.dto.OrbatSimpleDTO(o.id, o.name)
       FROM OrbatStructure o
       WHERE o.formationType = 'corps'
       AND o.commandName = :commandName
       ORDER BY o.name
       """)
    List<OrbatSimpleDTO> findCorpsByCommandName(String commandName);



    @Query("select distinct o.commandName from OrbatStructure o where o.commandName is not null")
    List<String> findDistinctCommandNames();

    @Query("select distinct o.corpsName from OrbatStructure o where o.corpsName is not null")
    List<String> findDistinctCorpsNames();

    @Query("select distinct o.divisionName from OrbatStructure o where o.divisionName is not null")
    List<String> findDistinctDivisionNames();


    // Case-insensitive search by formation name
    @Query("SELECT o FROM OrbatStructure o WHERE LOWER(o.name) = LOWER(:formationName)")
    Optional<OrbatStructure> findByFormationNameCaseInsensitive(@Param("formationName") String formationName);

    // Check if formation exists (case-insensitive)
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM OrbatStructure o WHERE LOWER(o.name) = LOWER(:formationName)")
    boolean existsByFormationNameCaseInsensitive(@Param("formationName") String formationName);

    // Search by partial name (for autocomplete)
    @Query("SELECT o FROM OrbatStructure o WHERE LOWER(o.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    java.util.List<OrbatStructure> searchByFormationName(@Param("searchTerm") String searchTerm);


}
