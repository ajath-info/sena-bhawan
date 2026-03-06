package com.example.sena_bhawan.repository.formation;

import com.example.sena_bhawan.entity.formation.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DivisionRepository extends JpaRepository<Division,Long> {
    List<Division> findAll();
}
