package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.UserMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserMasterRepository extends JpaRepository<UserMaster, Long> {
    Optional<UserMaster> findByUsername(String username);
}
