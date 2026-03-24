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
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseEligibilityService {

    private final CourseMasterRepository courseRepository;
    private final UnitMasterRepository unitRepository;
    private final DropdownMasterRepository dropdownRepository;

    private final CourseEligibilityRepo eligibilityRepository;
    private final CourseDropdownMappingRepository dropdownMappingRepository;
    private final PersonnelCourseRepository personnelCourseRepository;

    @Transactional
    public CourseEligibilityMaster saveEligibility(CourseEligibilityDTO dto) {

        // Get the course
        CourseMaster course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + dto.getCourseId()));

        // Delete all existing mappings for this course
        deleteExistingMappings(course.getSrno());

        // Save or update Course Eligibility Master (only course-specific fields)
        CourseEligibilityMaster eligibility = saveOrUpdateEligibilityMaster(course, dto);

        // Collect ALL dropdown mappings in a single list
        List<CourseDropdownMapping> allDropdownMappings = new ArrayList<>();

        // Add mappings from each category
        allDropdownMappings.addAll(saveDropdownMappings(course, dto.getRankIds(), "RANK"));
        allDropdownMappings.addAll(saveDropdownMappings(course, dto.getPostingTypeIds(), "POST_TYPES"));
        allDropdownMappings.addAll(saveDropdownMappings(course, dto.getMinCourseGrading(), "COURSE_GRADE"));
        allDropdownMappings.addAll(saveDropdownMappings(course, dto.getEducationalQualifications(), "CIVIL_QUALIFICATIONS"));
//        allDropdownMappings.addAll(saveDropdownMappings(course, dto.getMedicalCategories(), "MEDICAL"));
        allDropdownMappings.addAll(saveDropdownMappings(course, dto.getEstablishmentTypes(), "ESTABLISHMENT"));
        allDropdownMappings.addAll(saveDropdownMappings(course, dto.getUnitIds(), "UNIT_TYPE"));

        // Handle remarks
        if (dto.getRemarks() != null && !dto.getRemarks().isEmpty()) {
            List<Long> remarkIds = getRemarkDropdownIds(dto.getRemarks());
            allDropdownMappings.addAll(saveDropdownMappings(course, remarkIds, "REMARKS"));
        }

        // Save ALL dropdown mappings in a single batch
        if (!allDropdownMappings.isEmpty()) {
            dropdownMappingRepository.saveAll(allDropdownMappings);
        }

        // Save Unit Mappings (if units are not in dropdown_master)
        savePersonnelMappings(course, dto.getMedicalCategories());

        return eligibility;
    }

    private void deleteExistingMappings(Integer courseId) {
        // Delete from all mapping tables
        personnelCourseRepository.deleteByCourseId(courseId);
        dropdownMappingRepository.deleteByCourseId(courseId); // This deletes all dropdown mappings
    }

    private CourseEligibilityMaster saveOrUpdateEligibilityMaster(CourseMaster course, CourseEligibilityDTO dto) {
        // Check if eligibility record already exists for this course
        CourseEligibilityMaster eligibility = eligibilityRepository.findByCourse(course);

        // If no record exists, create a new one
        if (eligibility == null) {
            eligibility = new CourseEligibilityMaster();
        }

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

    private void savePersonnelMappings(CourseMaster course, List<String> medicalCodes) {
        if (medicalCodes != null && !medicalCodes.isEmpty()) {
            // Create mappings directly from medical codes, not from personnel IDs
            List<PersonnelCourseMapping> mappings = medicalCodes.stream()
                    .map(medicalCode -> {
                        PersonnelCourseMapping mapping = new PersonnelCourseMapping();
                        mapping.setCourse(course);
                        mapping.setMedicalCode(medicalCode);
                        return mapping;
                    })
                    .collect(Collectors.toList());

            personnelCourseRepository.saveAll(mappings);
            log.debug("Saved {} medical code mappings for Course ID: {}", mappings.size(), course.getSrno());
        }
    }

    private List<CourseDropdownMapping> saveDropdownMappings(CourseMaster course, List<Long> dropdownIds, String dropdownType) {
        List<CourseDropdownMapping> mappings = new ArrayList<>();

        if (dropdownIds != null && !dropdownIds.isEmpty()) {
            for (Long dropdownId : dropdownIds) {
                DropdownMaster dropdown = dropdownRepository.findById(dropdownId)
                        .orElseThrow(() -> new EntityNotFoundException(
                                String.format("Dropdown not found with ID: %s for type: %s", dropdownId, dropdownType)));

                CourseDropdownMapping mapping = new CourseDropdownMapping();
                mapping.setCourse(course);
                mapping.setDropdown(dropdown);
                mappings.add(mapping);
            }
            log.debug("Created {} mappings for type: {}", mappings.size(), dropdownType);
        }

        return mappings;
    }

    private List<Long> getRemarkDropdownIds(List<String> remarks) {
        List<Long> remarkIds = new ArrayList<>();
        for (String remark : remarks) {
            dropdownRepository.findByTypeAndName("REMARKS", remark)
                    .ifPresent(dropdown -> remarkIds.add(dropdown.getId()));
        }
        return remarkIds;
    }



    // Add this method to CourseEligibilityService
    // Add this method to CourseEligibilityService
    public CourseEligibilityDTO getEligibilityByCourseId(Integer courseId) {

        CourseMaster course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));

        CourseEligibilityMaster eligibility = eligibilityRepository.findByCourse(course);
        if (eligibility == null) {
            throw new EntityNotFoundException("Eligibility not found for course ID: " + courseId);
        }

        CourseEligibilityDTO dto = new CourseEligibilityDTO();
        dto.setCourseId(courseId);

        // Set date filters
        dto.setCommissionDateFrom(eligibility.getCommissionDateFrom());
        dto.setCommissionDateTo(eligibility.getCommissionDateTo());
        dto.setSeniorityDateFrom(eligibility.getSeniorityDateFrom());
        dto.setSeniorityDateTo(eligibility.getSeniorityDateTo());
        dto.setDobFrom(eligibility.getDobFrom());
        dto.setDobTo(eligibility.getDobTo());

        dto.setAdditionalRemarks(eligibility.getAdditionalRemarks());

        // Get rank IDs from dropdown mapping table
        List<Long> rankIds = dropdownMappingRepository.findDropdownIdsByCourseIdAndType(courseId, "RANK");
        dto.setRankIds(rankIds);

        // Get medical codes from personnel_course_mapping (these are medical codes, not personnel IDs)
        List<String> medicalCodes = personnelCourseRepository.findMedicalCodesByCourseId(courseId);
        dto.setMedicalCategories(medicalCodes);

        List<Long> unitTypeId = dropdownMappingRepository.findDropdownIdsByCourseIdAndType(courseId, "UNIT_TYPE");
        dto.setUnitIds(unitTypeId);

        // Get posting type IDs from dropdown mapping
        List<Long> postingTypeIds = dropdownMappingRepository.findDropdownIdsByCourseIdAndType(courseId, "POST_TYPES");
        dto.setPostingTypeIds(postingTypeIds);

        // Get course grading IDs
        List<Long> minCourseGradingIds = dropdownMappingRepository.findDropdownIdsByCourseIdAndType(courseId, "COURSE_GRADE");
        dto.setMinCourseGrading(minCourseGradingIds);

        // Get educational qualification IDs
        List<Long> educationalQualificationIds = dropdownMappingRepository.findDropdownIdsByCourseIdAndType(courseId, "CIVIL_QUALIFICATIONS");
        dto.setEducationalQualifications(educationalQualificationIds);

        // Get establishment type IDs
        List<Long> establishmentTypeIds = dropdownMappingRepository.findDropdownIdsByCourseIdAndType(courseId, "ESTABLISHMENT");
        dto.setEstablishmentTypes(establishmentTypeIds);

        // Get remarks (from dropdown_master with type "REMARKS")
        List<Long> remarkIds = dropdownMappingRepository.findDropdownIdsByCourseIdAndType(courseId, "REMARKS");
        if (!remarkIds.isEmpty()) {
            List<String> remarks = remarkIds.stream()
                    .map(id -> dropdownRepository.findById(id)
                            .map(DropdownMaster::getName)
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            dto.setRemarks(remarks);
        }

        log.info("Successfully fetched eligibility for course ID: {}", courseId);
        return dto;
    }
}