package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonnelRepository extends JpaRepository<Personnel, Long> {

    @Query("SELECT p.rank AS rank, COUNT(p) AS count " +
            "FROM Personnel p GROUP BY p.rank")
    List<Object[]> getRankCounts();
}

