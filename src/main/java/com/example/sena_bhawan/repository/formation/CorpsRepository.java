package com.example.sena_bhawan.repository.formation;

import com.example.sena_bhawan.entity.formation.Corps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorpsRepository extends JpaRepository<Corps,Long> {
    List<Corps> findAll();
}
