package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.PostingDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PostingDetailsRepository extends JpaRepository<PostingDetails, Long> {

    List<PostingDetails> findByPersonnelId(Long personnelId);

    Optional<Object> findTopByPersonnelIdOrderByFromDateDesc(Long id);

    // Count active postings (current date between from_date and to_date)
    @Query("SELECT COUNT(p) FROM PostingDetails p " +
            "WHERE :currentDate BETWEEN p.fromDate AND p.toDate")
    long countActivePostings(@Param("currentDate") LocalDate currentDate);

    // Alternative: If to_date can be null (ongoing posting)
    @Query("SELECT COUNT(p) FROM PostingDetails p " +
            "WHERE p.fromDate <= :currentDate " +
            "AND (p.toDate IS NULL OR p.toDate >= :currentDate)")
    long countActivePostingsWithNullToDate(@Param("currentDate") LocalDate currentDate);

    // Count all records where from_date is in the future
    @Query("SELECT COUNT(p) FROM PostingDetails p WHERE p.fromDate > :currentDate")
    long countPendingTransfers(@Param("currentDate") LocalDate currentDate);
}
