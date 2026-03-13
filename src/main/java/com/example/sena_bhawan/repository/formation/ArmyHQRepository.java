package com.example.sena_bhawan.repository.formation;

import com.example.sena_bhawan.entity.formation.ArmyHq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArmyHQRepository extends JpaRepository<ArmyHq,Long> {
    List<ArmyHq> findAll();
}
