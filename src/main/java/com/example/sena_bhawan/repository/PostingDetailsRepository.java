package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.Personnel;
import com.example.sena_bhawan.entity.PostingDetails;
import com.example.sena_bhawan.projection.PostingDetailsProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PostingDetailsRepository extends JpaRepository<PostingDetails, Long> {
    List<PostingDetailsProjection> findByPersonnelIdInAndStatus(List<Long> personnelIds, String status);
    List<PostingDetails> findByPersonnelId(Long personnelId);

    @Query(value = """
        SELECT * FROM posting_details 
        WHERE personnel_id = :personnelId 
        AND status = 'PREVIOUS_POSTING'
        AND posting_id < :currentPostingId
        ORDER BY tos_updated_date DESC 
        LIMIT 1
    """, nativeQuery = true)
    Optional<PostingDetails> findLatestPreviousPostingBeforeId(
            @Param("personnelId") Long personnelId,
            @Param("currentPostingId") Long currentPostingId
    );

    // ✅ Find latest POSTED
    @Query(value = """
        SELECT * FROM posting_details 
        WHERE personnel_id = :personnelId 
        AND status = 'POSTED'
        ORDER BY tos_updated_date DESC 
        LIMIT 1
    """, nativeQuery = true)
    Optional<PostingDetails> findLatestPosted(@Param("personnelId") Long personnelId);

    // ✅ Find current UNDER_POSTING
    @Query("SELECT p FROM PostingDetails p WHERE p.personnelId = :personnelId AND p.status = 'UNDER_POSTING'")
    Optional<PostingDetails> findCurrentUnderPosting(@Param("personnelId") Long personnelId);

    // ✅ Find latest POSTED before a specific ID
    @Query(value = """
        SELECT * FROM posting_details 
        WHERE personnel_id = :personnelId 
        AND status = 'POSTED'
        AND posting_id < :currentPostingId
        ORDER BY tos_updated_date DESC 
        LIMIT 1
    """, nativeQuery = true)
    Optional<PostingDetails> findLatestPostedBeforeId(
            @Param("personnelId") Long personnelId,
            @Param("currentPostingId") Long currentPostingId
    );

    // ✅ Find the immediate previous POSTED record (the one before current)
    @Query(value = """
        SELECT * FROM posting_details 
        WHERE personnel_id = :personnelId 
        AND status = 'POSTED'
        AND posting_id < :currentPostingId
        ORDER BY tos_updated_date DESC 
        LIMIT 1
    """, nativeQuery = true)
    Optional<PostingDetails> findImmediatePreviousPosted(
            @Param("personnelId") Long personnelId,
            @Param("currentPostingId") Long currentPostingId
    );

    // ✅ Order by tosUpdatedDate instead of fromDate
    List<PostingDetails> findByPersonnelIdOrderByTosUpdatedDateDesc(Long personnelId);

//    // Find current UNDER_POSTING
//    @Query("SELECT p FROM PostingDetails p WHERE p.personnelId = :personnelId AND p.status = 'UNDER_POSTING'")
//    Optional<PostingDetails> findCurrentUnderPosting(@Param("personnelId") Long personnelId);
//
//    // Find latest POSTED (using tosUpdatedDate)
//    @Query("SELECT p FROM PostingDetails p WHERE p.personnelId = :personnelId AND p.status = 'POSTED' ORDER BY p.tosUpdatedDate DESC")
//    Optional<PostingDetails> findLatestPosted(@Param("personnelId") Long personnelId);


    // Find all previous postings for history
    List<PostingDetails> findByPersonnelIdAndStatusOrderByTosUpdatedDateDesc(Long personnelId, String status);

    Optional<Object> findTopByPersonnelIdOrderByFromDateDesc(Long id);

    @Query("""
           SELECT DISTINCT p.personnelId
           FROM PostingDetails p
           WHERE p.unitName = :unitName
           """)
    List<Long> findActivePersonnelIdsByUnitName(
            @Param("unitName") String unitName);

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


    @Query("SELECT DISTINCT pd.personnelId FROM PostingDetails pd " +
            "WHERE pd.formationType = :formationType AND pd.unitName = :unitName")
    List<Long> findPersonnelIdsByFormationTypeAndUnitName(
            @Param("formationType") String formationType,
            @Param("unitName") String unitName);

    @Query("SELECT pd FROM PostingDetails pd " +
            "WHERE pd.personnelId IN :personnelIds AND pd.formationType = :formationType " +
            "AND pd.unitName = :unitName ORDER BY pd.fromDate DESC")
    List<PostingDetails> findPostingsByPersonnelIdsAndUnit(
            @Param("personnelIds") List<Long> personnelIds,
            @Param("formationType") String formationType,
            @Param("unitName") String unitName);

    // ✅ Sorted order ke liye ye method chahiye
    List<PostingDetails> findByPersonnelIdOrderByFromDateDesc(Long personnelId);

    // Find current posting (status = UNDER_POSTING)
    Optional<PostingDetails> findByPersonnelIdAndStatus(Long personnelId, String status);

    // Find current posting using @Query
    @Query("SELECT p FROM PostingDetails p WHERE p.personnelId = :personnelId AND p.status = 'UNDER_POSTING'")
    Optional<PostingDetails> findCurrentPosting(@Param("personnelId") Long personnelId);

    // Find all historical postings (status = POSTED)
    @Query("SELECT p FROM PostingDetails p WHERE p.personnelId = :personnelId AND p.status IN ('PREVIOUS_POSTING') ORDER BY p.tosUpdatedDate DESC NULLS LAST")
    List<PostingDetails> findPostingHistory(@Param("personnelId") Long personnelId);

    // Check if personnel has any posting
    boolean existsByPersonnelId(Long personnelId);

    // Complete current posting (change status to POSTED)
    @Modifying
    @Transactional
    @Query("UPDATE PostingDetails p SET p.status = 'POSTED', p.toDate = :tosDate, p.sosDate = :tosDate WHERE p.personnelId = :personnelId AND p.status = 'UNDER_POSTING'")
    int completeCurrentPosting(@Param("personnelId") Long personnelId, @Param("tosDate") LocalDate tosDate);

    // Find by ORBAT ID (for reporting)
    List<PostingDetails> findByOrbatIdAndStatus(Long orbatId, String status);

    // Count personnel in a unit
    @Query("SELECT COUNT(p) FROM PostingDetails p WHERE p.orbatId = :orbatId AND p.status = 'UNDER_POSTING'")
    long countCurrentPersonnelInUnit(@Param("orbatId") Long orbatId);

    }
