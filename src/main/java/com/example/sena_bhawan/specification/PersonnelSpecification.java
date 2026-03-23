package com.example.sena_bhawan.specification;

import com.example.sena_bhawan.dto.PersonnelFilterRequest;
import com.example.sena_bhawan.entity.Personnel;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PersonnelSpecification {
    
    public static Specification<Personnel> filterPersonnel(PersonnelFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Search by armyNo
            if (filter.armyNo != null && !filter.armyNo.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("armyNo")), 
                    "%" + filter.armyNo.toLowerCase() + "%"));
            }
            
            // Search by placeOfBirth
            if (filter.placeOfBirth != null && !filter.placeOfBirth.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("placeOfBirth")), 
                    "%" + filter.placeOfBirth.toLowerCase() + "%"));
            }
            
            // Date of Birth
            if (filter.dobGreaterThan != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateOfBirth"), filter.dobGreaterThan));
            }
            
            // Date of Commission range
            if (filter.docFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateOfCommission"), filter.docFrom));
            }
            if (filter.docTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateOfCommission"), filter.docTo));
            }
            
            // Date of Seniority range
            if (filter.dosFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateOfSeniority"), filter.dosFrom));
            }
            if (filter.dosTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateOfSeniority"), filter.dosTo));
            }
            
            // Rank IN clause
            if (filter.rank != null && !filter.rank.isEmpty()) {
                CriteriaBuilder.In<String> inClause = cb.in(root.get("rank"));
                for (String rank : filter.rank) {
                    inClause.value(rank);
                }
                predicates.add(inClause);
            }
            
            // Medical Category IN clause
            if (filter.medicalCategory != null && !filter.medicalCategory.isEmpty()) {
                CriteriaBuilder.In<String> inClause = cb.in(root.get("medicalCode"));
                for (String medical : filter.medicalCategory) {
                    inClause.value(medical);
                }
                predicates.add(inClause);
            }
            
            // General search (armyNo, fullName, rank)
            if (filter.search != null && !filter.search.isEmpty()) {
                String searchPattern = "%" + filter.search.toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("armyNo")), searchPattern),
                    cb.like(cb.lower(root.get("fullName")), searchPattern),
                    cb.like(cb.lower(root.get("rank")), searchPattern)
                ));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}