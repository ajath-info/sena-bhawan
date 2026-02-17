package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.UnitEstablishment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnitEstablishmentRepository extends JpaRepository<UnitEstablishment,Long> {
    Optional<UnitEstablishment> findByUnitId_IdAndEstablishmentType(
            Long unitId,
            UnitEstablishment.EstablishmentType type
    );

    @Query("SELECT u FROM UnitEstablishment u WHERE u.unitId.id = :unitId")
    UnitEstablishment findByUnitId(@Param("unitId") Long unitId);

    Optional<UnitEstablishment> findByUnitIdAndEstablishmentType(Long unitId, UnitEstablishment.EstablishmentType establishmentType);


}
