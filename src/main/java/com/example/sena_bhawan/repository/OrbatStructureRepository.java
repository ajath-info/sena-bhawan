package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.OrbatStructure;
import com.example.sena_bhawan.dto.OrbatSimpleDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrbatStructureRepository
        extends JpaRepository<OrbatStructure, Long> {

    Optional<OrbatStructure> findById(Long id);

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

}
