package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.UnitMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitMasterRepository extends JpaRepository<UnitMaster, Long> {

}

