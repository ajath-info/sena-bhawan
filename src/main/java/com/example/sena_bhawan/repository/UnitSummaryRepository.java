package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.PostingDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UnitSummaryRepository extends JpaRepository<PostingDetails, Long> {

    // Get all personnel IDs in this unit
    @Query("SELECT DISTINCT pd.personnelId FROM PostingDetails pd WHERE pd.formationType = :formationType AND pd.unitName = :unitName")
    List<Long> findPersonnelIdsByUnit(@Param("formationType") String formationType, @Param("unitName") String unitName);

    // Get SOS dates for personnel (previous posting's from_date)
    @Query("SELECT pd.personnelId, pd.fromDate FROM PostingDetails pd WHERE pd.personnelId IN :personnelIds AND pd.toDate IS NOT NULL ORDER BY pd.fromDate DESC")
    List<Object[]> findPreviousPostingDates(@Param("personnelIds") List<Long> personnelIds);

    // Get current postings in this unit
    @Query("SELECT pd FROM PostingDetails pd WHERE pd.formationType = :formationType AND pd.unitName = :unitName AND pd.toDate IS NULL")
    List<PostingDetails> findCurrentPostingsInUnit(@Param("formationType") String formationType, @Param("unitName") String unitName);
}