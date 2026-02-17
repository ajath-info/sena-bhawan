package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.RemarksUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RemarksUpdateRepository extends JpaRepository<RemarksUpdate,Long> {
    Optional<RemarksUpdate> findByPersonnelId(Long personnelId);
}
