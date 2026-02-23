package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.CourseEligibilityDTO;
import com.example.sena_bhawan.entity.*;
import com.example.sena_bhawan.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseEligibilityService {

    private final CourseMasterRepository courseRepository;
    private final RankMasterRepository rankRepository;
    private final UnitMasterRepository unitRepository;
    private final DropdownMasterRepository dropdownRepository;

    private final CourseEligibilityRepo eligibilityRepository;
    private final CourseRankMappingRepository rankMappingRepository;
    private final CourseUnitMappingRepository unitMappingRepository;
    private final CourseDropdownMappingRepository dropdownMappingRepository;

    @Transactional
    public CourseEligibilityMaster saveEligibility(CourseEligibilityDTO dto) {
        log.info("Saving eligibility for course ID: {}", dto.getCourseId());

        // Get the course
        CourseMaster course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + dto.getCourseId()));

        // Delete all existing mappings for this course
        deleteExistingMappings(course.getSrno());

        // Save or update Course Eligibility Master (only course-specific fields)
        CourseEligibilityMaster eligibility = saveOrUpdateEligibilityMaster(course, dto);

        // Save Rank Mappings
        saveRankMappings(course, dto.getRankIds());

        // Save Unit Mappings
        saveUnitMappings(course, dto.getUnitIds());

        // Save all dropdown mappings using the single repository
        saveDropdownMappings(course, dto.getPostingTypeIds(), "POST_TYPES");
        saveDropdownMappings(course, dto.getMinCourseGrading(), "COURSE_GRADE");
        saveDropdownMappings(course, dto.getEducationalQualifications(), "CIVIL_QUALIFICATION");
        saveDropdownMappings(course, dto.getMedicalCategories(), "MEDICAL");
        saveDropdownMappings(course, dto.getEstablishmentTypes(), "ESTABLISHMENT");

        // For remarks (if they come from dropdown_master with type "REMARKS")
        if (dto.getRemarks() != null && !dto.getRemarks().isEmpty()) {
            // You need to get the dropdown IDs for these remark values
            List<Long> remarkIds = getRemarkDropdownIds(dto.getRemarks());
            saveDropdownMappings(course, remarkIds, "REMARKS");
        }

        return eligibility;
    }

    private void deleteExistingMappings(Integer courseId) {

        // Delete from all mapping tables
        rankMappingRepository.deleteByCourseId(courseId);
        unitMappingRepository.deleteByCourseId(courseId);
        dropdownMappingRepository.deleteByCourseId(courseId); // This deletes all dropdown mappings

        // Note: We don't delete from course_eligibility_master - we update it
    }

    private CourseEligibilityMaster saveOrUpdateEligibilityMaster(CourseMaster course, CourseEligibilityDTO dto) {
        // Check if eligibility record already exists for this course
        CourseEligibilityMaster eligibility = eligibilityRepository.findByCourse(course);

        eligibility.setCourse(course);

        // Set date filters
        eligibility.setCommissionDateFrom(dto.getCommissionDateFrom());
        eligibility.setCommissionDateTo(dto.getCommissionDateTo());
        eligibility.setSeniorityDateFrom(dto.getSeniorityDateFrom());
        eligibility.setSeniorityDateTo(dto.getSeniorityDateTo());
        eligibility.setDobFrom(dto.getDobFrom());
        eligibility.setDobTo(dto.getDobTo());

        // Set additional remarks
        eligibility.setAdditionalRemarks(dto.getAdditionalRemarks());

        return eligibilityRepository.save(eligibility);
    }

    private void saveRankMappings(CourseMaster course, List<Long> rankIds) {
        if (rankIds != null && !rankIds.isEmpty()) {
            List<CourseRankMapping> mappings = rankIds.stream()
                    .map(rankId -> {
                        RankMaster rank = rankRepository.findById(rankId)
                                .orElseThrow(() -> new EntityNotFoundException("Rank not found with ID: " + rankId));

                        CourseRankMapping mapping = new CourseRankMapping();
                        mapping.setCourse(course);
                        mapping.setRank(rank);
                        return mapping;
                    })
                    .collect(Collectors.toList());

            rankMappingRepository.saveAll(mappings);
            log.debug("Saved {} rank mappings for course ID: {}", mappings.size(), course.getSrno());
        }
    }

    private void saveUnitMappings(CourseMaster course, List<Long> unitIds) {
        if (unitIds != null && !unitIds.isEmpty()) {
            List<CourseUnitMapping> mappings = unitIds.stream()
                    .map(unitId -> {
                        UnitMaster unit = unitRepository.findById(unitId)
                                .orElseThrow(() -> new EntityNotFoundException("Unit not found with ID: " + unitId));

                        CourseUnitMapping mapping = new CourseUnitMapping();
                        mapping.setCourse(course);
                        mapping.setUnit(unit);
                        return mapping;
                    })
                    .collect(Collectors.toList());

            unitMappingRepository.saveAll(mappings);
            log.debug("Saved {} unit mappings for course ID: {}", mappings.size(), course.getSrno());
        }
    }

    private void saveDropdownMappings(CourseMaster course, List<Long> dropdownIds, String dropdownType) {
        if (dropdownIds != null && !dropdownIds.isEmpty()) {
            List<CourseDropdownMapping> mappings = new ArrayList<>();

            for (Long dropdownId : dropdownIds) {
                DropdownMaster dropdown = dropdownRepository.findById(dropdownId)
                        .orElseThrow(() -> new EntityNotFoundException(
                                String.format("Dropdown not found with ID: %s for type: %s", dropdownId, dropdownType)));

                CourseDropdownMapping mapping = new CourseDropdownMapping();
                mapping.setCourse(course);
                mapping.setDropdown(dropdown);
                mappings.add(mapping);
            }

            dropdownMappingRepository.saveAll(mappings);

        }
    }

    private List<Long> getRemarkDropdownIds(List<String> remarks) {
        // This method fetches dropdown IDs for the given remark values
        // You need to implement this based on how remarks are stored in dropdown_master
        List<Long> remarkIds = new ArrayList<>();
        for (String remark : remarks) {
            dropdownRepository.findByTypeAndName("REMARKS", remark)
                    .ifPresent(dropdown -> remarkIds.add(dropdown.getId()));
        }
        return remarkIds;
    }
}
//
//    // Method to retrieve eligibility data
//    public CourseEligibilityDTO getEligibilityByCourseId(Integer courseId) {
//        CourseMaster course = courseRepository.findById(courseId)
//                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));
//
//        CourseEligibilityMaster eligibility = eligibilityRepository.findByCourse(course);
//
//        CourseEligibilityDTO dto = new CourseEligibilityDTO();
//        dto.setCourseId(courseId);
//
//        // Set date filters
//        dto.setCommissionDateFrom(eligibility.getCommissionDateFrom());
//        dto.setCommissionDateTo(eligibility.getCommissionDateTo());
//        dto.setSeniorityDateFrom(eligibility.getSeniorityDateFrom());
//        dto.setSeniorityDateTo(eligibility.getSeniorityDateTo());
//        dto.setDobFrom(eligibility.getDobFrom());
//        dto.setDobTo(eligibility.getDobTo());
//
//        dto.setAdditionalRemarks(eligibility.getAdditionalRemarks());
//
//        // Get rank IDs
//        dto.setRankIds(rankMappingRepository.findRankIdsByCourseId(courseId));
//
//        // Get unit IDs
//        dto.setUnitIds(unitMappingRepository.findUnitIdsByCourseId(courseId));
//
//        // Get all dropdown mappings by type
//        dto.setPostingTypeIds(dropdownMappingRepository.findDropdownIdsByCourseIdAndType(courseId, "POST_TYPES"));
//        dto.setMinCourseGrading(dropdownMappingRepository.findDropdownIdsByCourseIdAndType(courseId, "COURSE_GRADE"));
//        dto.setEducationalQualifications(dropdownMappingRepository.findDropdownIdsByCourseIdAndType(courseId, "CIVIL_QUALIFICATION"));
//        dto.setMedicalCategories(dropdownMappingRepository.findDropdownIdsByCourseIdAndType(courseId, "MEDICAL"));
//        dto.setEstablishmentTypes(dropdownMappingRepository.findDropdownIdsByCourseIdAndType(courseId, "ESTABLISHMENT"));
//
//        // Get remarks (if stored in dropdown_master)
//        List<Long> remarkIds = dropdownMappingRepository.findDropdownIdsByCourseIdAndType(courseId, "REMARKS");
//        if (!remarkIds.isEmpty()) {
//            List<String> remarks = remarkIds.stream()
//                    .map(id -> dropdownRepository.findById(id).map(DropdownMaster::getName).orElse(null))
//                    .filter(name -> name != null)
//                    .collect(Collectors.toList());
//            dto.setRemarks(remarks);
//        }
//
//        return dto;
//    }
//}