package com.example.sena_bhawan.repository.formation;

import com.example.sena_bhawan.entity.formation.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit,Long> {
    List<Unit> findAll();
}
