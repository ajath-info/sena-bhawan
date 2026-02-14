package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.OfficerPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfficerPostingRepository extends JpaRepository<OfficerPosting, Long> {
}
