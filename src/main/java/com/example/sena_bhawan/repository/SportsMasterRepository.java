package com.example.sena_bhawan.repository;
import com.example.sena_bhawan.entity.SportsMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SportsMasterRepository
        extends JpaRepository<SportsMaster, Long> {

    @Query("""
        SELECT s.sportName
        FROM Sports s
        WHERE s.isActive = true
        ORDER BY s.sportName
    """)
    List<String> findActiveSports();
}
