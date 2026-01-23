package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.DecorationMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DecorationMasterRepository extends JpaRepository<DecorationMaster, Long> {
}
