package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.FormationEstablishment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormationEstablishmentRepository extends JpaRepository<FormationEstablishment,Long> {
    Optional<FormationEstablishment>
    findByOrbatIdAndEstablishmentType(
            Long orbatId,
            FormationEstablishment.EstablishmentType type
    );
}
