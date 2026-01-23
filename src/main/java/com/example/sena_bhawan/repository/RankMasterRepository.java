package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.RankMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RankMasterRepository extends JpaRepository<RankMaster, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM RankMaster")
    void clearAll();

    RankMaster findByRank(String rank);

}

