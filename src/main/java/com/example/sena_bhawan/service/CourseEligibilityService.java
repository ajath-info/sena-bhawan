package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.CourseEligibilityDTO;
import com.example.sena_bhawan.entity.*;
import com.example.sena_bhawan.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseEligibilityService {

    private final CourseEligibilityRepo eligibilityRepo;
    private final CourseMasterRepository courseRepo;
    private final RankMasterRepository rankRepo;
    private final UnitMasterRepository unitRepo;
    private final PostingTypeMasterRepo postingRepo;

    public CourseEligibilityMaster saveEligibility(CourseEligibilityDTO dto) {

        CourseMaster course = courseRepo.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // find existing eligibility
        CourseEligibilityMaster eligibility = eligibilityRepo.findByCourse_Srno(dto.getCourseId())
                .orElse(new CourseEligibilityMaster());

        eligibility.setCourse(course);
        eligibility.setMinYears(dto.getMinYears());
        eligibility.setMaxYears(dto.getMaxYears());
        eligibility.setMinCourseGrading(dto.getMinCourseGrading());
        eligibility.setEducationalQualification(dto.getEducationalQualification());
        eligibility.setMaxServiceLimit(dto.getMaxServiceLimit());
        eligibility.setMedicalCategory(dto.getMedicalCategory());
        eligibility.setAdditionalRemarks(dto.getAdditionalRemarks());
        eligibility.setUpdatedAt(LocalDateTime.now());

        // Clear old lists (update scenario)
        eligibility.getEligibleRanks().forEach(r -> r.setEligibility(null));
        eligibility.getEligibleUnits().forEach(u -> u.setEligibility(null));
        eligibility.getPostingTypes().forEach(p -> p.setEligibility(null));

        eligibility.getEligibleRanks().clear();
        eligibility.getEligibleUnits().clear();
        eligibility.getPostingTypes().clear();


        // RANKS
        List<CourseEligibleRank> ranks = dto.getRankIds().stream().map(id -> {
            RankMaster r = rankRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Rank not found"));
            CourseEligibleRank er = new CourseEligibleRank();
            er.setEligibility(eligibility);
            er.setRank(r);
            return er;
        }).toList();

        eligibility.setEligibleRanks(ranks);

        // UNITS
        List<CourseEligibleUnit> units = dto.getUnitIds().stream().map(id -> {
            UnitMaster u = unitRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Unit not found"));
            CourseEligibleUnit eu = new CourseEligibleUnit();
            eu.setEligibility(eligibility);
            eu.setUnit(u);
            return eu;
        }).toList();

        eligibility.setEligibleUnits(units);

        // POSTING TYPES
        List<CourseEligiblePostingType> postings = dto.getPostingTypeIds().stream().map(id -> {
            PostingTypeMaster p = postingRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Posting type not found"));
            CourseEligiblePostingType ep = new CourseEligiblePostingType();
            ep.setEligibility(eligibility);
            ep.setPostingType(p);
            return ep;
        }).toList();

        eligibility.setPostingTypes(postings);

        return eligibilityRepo.save(eligibility);
    }
}

