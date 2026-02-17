package com.example.sena_bhawan.repository;
import com.example.sena_bhawan.entity.EstablishmentTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface EstablishmentTypeMasterRepository
        extends JpaRepository<EstablishmentTypeMaster, Long> {

    @Query("""
        SELECT e.estName
        FROM EstablishmentType e
        WHERE e.isActive = true
        ORDER BY e.estName
    """)
    List<String> findActiveEstablishments();
}
