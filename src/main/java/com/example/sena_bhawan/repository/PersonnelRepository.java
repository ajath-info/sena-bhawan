package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.Personnel;
import com.example.sena_bhawan.projection.AgeBandProjection;
import com.example.sena_bhawan.projection.MedicalCategoryProjection;
import com.example.sena_bhawan.projection.RetirementYearProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface PersonnelRepository
        extends JpaRepository<Personnel, Long>,
        JpaSpecificationExecutor<Personnel> {

    @Query("SELECT p.rank AS rank, COUNT(p) AS count " +
            "FROM Personnel p GROUP BY p.rank")
    List<Object[]> getRankCounts();

    List<Personnel> findByIdIn(List<Long> ids);

    @Query("SELECT COUNT(p) FROM Personnel p WHERE LOWER(p.rank) LIKE LOWER(CONCAT('%', :term, '%'))")
    long countByRankContaining(@Param("term") String term);

    @Query("SELECT COUNT(p) FROM Personnel p WHERE LOWER(p.rank) = LOWER(:rank)")
    long countByExactRank(@Param("rank") String rank);

    @Query("SELECT " +
            "SUM(CASE WHEN p.dateOfBirth > :date30 THEN 1 ELSE 0 END) as under30, " +
            "SUM(CASE WHEN p.dateOfBirth <= :date30 AND p.dateOfBirth > :date35 THEN 1 ELSE 0 END) as age31to35, " +
            "SUM(CASE WHEN p.dateOfBirth <= :date35 AND p.dateOfBirth > :date40 THEN 1 ELSE 0 END) as age36to40, " +
            "SUM(CASE WHEN p.dateOfBirth <= :date40 AND p.dateOfBirth > :date45 THEN 1 ELSE 0 END) as age41to45, " +
            "SUM(CASE WHEN p.dateOfBirth <= :date45 AND p.dateOfBirth > :date50 THEN 1 ELSE 0 END) as age46to50, " +
            "SUM(CASE WHEN p.dateOfBirth <= :date50 THEN 1 ELSE 0 END) as over50 " +
            "FROM Personnel p")
    AgeBandProjection getAllAgeBandCounts(@Param("date30") LocalDate date30,
                                          @Param("date35") LocalDate date35,
                                          @Param("date40") LocalDate date40,
                                          @Param("date45") LocalDate date45,
                                          @Param("date50") LocalDate date50);

    // Add this method to your existing PersonnelRepository
    @Query("SELECT p.medicalCategory as medicalCategory, COUNT(p) as count " +
            "FROM Personnel p " +
            "WHERE p.medicalCategory IS NOT NULL AND p.medicalCategory <> '' " +
            "GROUP BY p.medicalCategory")
    List<MedicalCategoryProjection> getMedicalCategoryCounts();

    // Method 3: Combined - uses seniority if available, otherwise commission
    @Query(value = "SELECT retirement_year as retirementYear, COUNT(*) as count FROM (" +
            "SELECT CASE " +
            "  WHEN date_of_seniority IS NOT NULL THEN EXTRACT(YEAR FROM (date_of_seniority + INTERVAL '10 years')) " +
            "  ELSE EXTRACT(YEAR FROM (date_of_commission + INTERVAL '10 years')) " +
            "END as retirement_year " +
            "FROM personnel " +
            "WHERE date_of_commission IS NOT NULL" +
            ") AS retirements " +
            "WHERE retirement_year BETWEEN :startYear AND :endYear " +
            "GROUP BY retirement_year " +
            "ORDER BY retirement_year", nativeQuery = true)
    List<RetirementYearProjection> getRetirementForecast(@Param("startYear") int startYear, @Param("endYear") int endYear);

    // Add to your existing PersonnelRepository
    @Query("SELECT COUNT(p) FROM Personnel p")
    long getTotalPersonnelCount();
}

