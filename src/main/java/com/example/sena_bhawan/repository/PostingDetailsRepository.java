package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.PostingDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostingDetailsRepository extends JpaRepository<PostingDetails, Long> {

    List<PostingDetails> findByPersonnelId(Long personnelId);

    Optional<Object> findTopByPersonnelIdOrderByFromDateDesc(Long id);


    @Query("""
           SELECT DISTINCT p.personnelId
           FROM PostingDetails p
           WHERE p.unitName = :unitName
           AND p.toDate IS NULL
           """)
    List<Long> findActivePersonnelIdsByUnitName(
            @Param("unitName") String unitName);

}






