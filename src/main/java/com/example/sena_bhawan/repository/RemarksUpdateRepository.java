package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.dto.RemarksHistoryDTO;
import com.example.sena_bhawan.entity.RemarksUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RemarksUpdateRepository extends JpaRepository<RemarksUpdate,Long> {
    List<RemarksUpdate> findByPersonnelIdOrderByIdDesc(@Param("personnelId") Long personnelId);
}
