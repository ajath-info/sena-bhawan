package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.Officer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfficerRepository extends JpaRepository<Officer, Integer> {

    List<Officer> findByStatus(String status);

    List<Officer> findByRank(String rank);

    List<Officer> findByUnitName(String unitName);
}

