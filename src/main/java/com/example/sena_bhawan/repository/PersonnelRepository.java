package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.dto.PersonnelFilterRequest;
import com.example.sena_bhawan.entity.OrbatStructure;
import com.example.sena_bhawan.entity.Personnel;
import com.example.sena_bhawan.projection.AgeBandProjection;
import com.example.sena_bhawan.projection.MedicalCategoryProjection;
import com.example.sena_bhawan.projection.RetirementYearProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface PersonnelRepository extends JpaRepository<Personnel, Long>, JpaSpecificationExecutor<Personnel> {

    @Query("""
    SELECT DISTINCT p
    FROM Personnel p
    WHERE LOWER(p.armyNo) LIKE LOWER(CONCAT(:term, '%'))
    ORDER BY p.armyNo
    """)
    List<Personnel> findDistinctByArmyNoStartingWith(@Param("term") String term);

    @Query("SELECT distinct medicalCode FROM Personnel")
    List<String> getMedicalCode();

    @Query("SELECT p.rank AS rank, COUNT(p) AS count " +
            "FROM Personnel p GROUP BY p.rank")
    List<Object[]> getRankCounts();


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

//    // Add this method to your existing PersonnelRepository
//    @Query("SELECT p.medicalCategory as medicalCategory, COUNT(p) as count " +
//            "FROM Personnel p " +
//            "WHERE p.medicalCategory IS NOT NULL AND p.medicalCategory <> '' " +
//            "GROUP BY p.medicalCategory")
//    List<MedicalCategoryProjection> getMedicalCategoryCounts();

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


    List<Personnel> findByIdIn(List<Long> personnelIds);

    @Query("SELECT MIN(p.dateOfSeniority) FROM Personnel p WHERE p.id IN :personnelIds")
    Optional<LocalDate> findEarliestSeniority(@Param("personnelIds") List<Long> personnelIds);

    @Query("SELECT MAX(p.dateOfSeniority) FROM Personnel p WHERE p.id IN :personnelIds")
    Optional<LocalDate> findLatestSeniority(@Param("personnelIds") List<Long> personnelIds);

    @Query("SELECT MIN(p.dateOfCommission) FROM Personnel p WHERE p.id IN :personnelIds")
    Optional<LocalDate> findEarliestCommission(@Param("personnelIds") List<Long> personnelIds);

    @Query("SELECT MAX(p.dateOfCommission) FROM Personnel p WHERE p.id IN :personnelIds")
    Optional<LocalDate> findLatestCommission(@Param("personnelIds") List<Long> personnelIds);

    Optional<Personnel> findByArmyNo(String armyNo);
    boolean existsByArmyNo(String armyNo);

    @Query(value = """
        SELECT 
            p.id AS id,
            p.army_no AS armyNo,
            p.rank AS rank,
            p.full_name AS fullName,
            TO_CHAR(p.date_of_birth, 'YYYY-MM-DD') AS dateOfBirth,
            TO_CHAR(p.date_of_commission, 'YYYY-MM-DD') AS dateOfCommission,
            TO_CHAR(p.date_of_seniority, 'YYYY-MM-DD') AS dateOfSeniority,
            COALESCE(p.medical_code, '-') AS medicalCode,
            COALESCE(p.religion, '-') AS religion,
            COALESCE(p.marital_status, '-') AS maritalStatus,
            COALESCE(p.mobile_number, '-') AS mobileNumber,
            COALESCE(p.email_address, '-') AS emailAddress,
            COALESCE(p.city, '-') AS city,
            COALESCE(p.state, '-') AS state,
            COALESCE(p.place_of_birth, '-') AS placeOfBirth,
            (
                            SELECT TO_CHAR(pd.tos_updated_date, 'YYYY-MM-DD')
                            FROM posting_details pd
                            WHERE pd.personnel_id = p.id\s
                                AND pd.tos_updated_date IS NOT NULL
                            ORDER BY pd.from_date DESC
                            LIMIT 1
                        ) AS tosDate,
                    
            -- Unit with area_type
            (
                SELECT jsonb_build_object('unit_name', os.name, 'area_type', COALESCE(os.area_type, '-'))
                FROM posting_details pd
                INNER JOIN orbat_structure os ON pd.orbat_id = os.id
                WHERE pd.personnel_id = p.id 
                    AND LOWER(pd.formation_type) = 'unit'
                ORDER BY pd.from_date DESC
                LIMIT 1
            ) AS unit,
            
            -- Division
            (
                SELECT td.division_name
                FROM posting_details pd
                INNER JOIN orbat_structure os ON pd.orbat_id = os.id
                INNER JOIN tb_division td ON os.division_code = td.div_code
                WHERE pd.personnel_id = p.id 
                    AND LOWER(pd.formation_type) = 'unit'
                ORDER BY pd.from_date DESC
                LIMIT 1
            ) AS division,
            
            -- Establishment Type
            (
                SELECT fe.establishment_type
                FROM posting_details pd
                INNER JOIN formation_establishment fe ON pd.orbat_id = fe.orbat_id
                WHERE pd.personnel_id = p.id 
                    AND LOWER(pd.formation_type) = 'unit'
                ORDER BY pd.from_date DESC
                LIMIT 1
            ) AS establishmentType,
            
            -- Command
            (
                SELECT tc.command_name
                FROM posting_details pd
                INNER JOIN orbat_structure os ON pd.orbat_id = os.id
                INNER JOIN tb_corps tcor ON os.corps_code = tcor.corps_code
                INNER JOIN tb_command tc ON tcor.command_id = tc.command_id
                WHERE pd.personnel_id = p.id 
                ORDER BY pd.from_date DESC
                LIMIT 1
            ) AS command,
            
            -- Corps
            (
                SELECT tcor.corps_name
                FROM posting_details pd
                INNER JOIN orbat_structure os ON pd.orbat_id = os.id
                INNER JOIN tb_corps tcor ON os.corps_code = tcor.corps_code
                WHERE pd.personnel_id = p.id 
                ORDER BY pd.from_date DESC
                LIMIT 1
            ) AS corps,
            
            -- Courses Completed
            (
                SELECT STRING_AGG(DISTINCT cm.course_name, ', ')
                FROM course_panel_nomination cpn
                INNER JOIN course_schedule cs ON cpn.schedule_id = cs.schedule_id
                INNER JOIN course_master cm ON cs.course_id = cm.srno
                WHERE cpn.personnel_id = p.id 
            ) AS course,
            
            -- Civil Qualifications
            (
                SELECT STRING_AGG(DISTINCT pq.qualification, ', ')
                FROM personnel_qualifications pq
                WHERE pq.personnel_id = p.id
            ) AS civilQual,
            
            -- Sports
            (
                SELECT STRING_AGG(DISTINCT ps.sport_name || ' (' || ps.level || ')', ', ')
                FROM personnel_sports ps
                WHERE ps.personnel_id = p.id
            ) AS sports
            
        FROM personnel p
        WHERE p.id IN (:ids)
        ORDER BY p.id DESC
        """, nativeQuery = true)
    List<Object[]> findPersonnelWithDetailsByIds(@Param("ids") List<Long> ids);

    @Query("SELECT p FROM Personnel p WHERE p.id IN :personnelIds")
    List<Personnel> findAllByIdIn(@Param("personnelIds") List<Long> personnelIds);

}

