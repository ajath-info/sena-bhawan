package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.ParamountDTO.*;
import com.example.sena_bhawan.service.PersonnelProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/personnel")
@RequiredArgsConstructor
public class PersonnelProfileController {

    private final PersonnelProfileService personnelProfileService;

    /**
     * Get Identity and Service Details
     * GET /api/personnel/{personnelId}/identity-service
     */
    @GetMapping("/{personnelId}/identity-service")
    public ResponseEntity<IdentityAndServiceDto> getIdentityAndService(
            @PathVariable Long personnelId) {
        try {
            IdentityAndServiceDto data = personnelProfileService.getIdentityAndService(personnelId);
            if (data == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            log.error("Error in getIdentityAndService: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get Posting History
     * GET /api/personnel/{personnelId}/posting-history
     */
    @GetMapping("/{personnelId}/paramount/posting-history")
    public ResponseEntity<List<PostingHistoryDto>> getPostingHistory(
            @PathVariable Long personnelId) {
        try {
            List<PostingHistoryDto> data = personnelProfileService.getPostingHistory(personnelId);
            return ResponseEntity.ok(data != null ? data : Collections.emptyList());
        } catch (Exception e) {
            log.error("Error in getPostingHistory: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    /**
     * Get Courses Completed
     * GET /api/personnel/{personnelId}/courses-completed
     */
    @GetMapping("/{personnelId}/courses-completed")
    public ResponseEntity<List<CourseCompletedDto>> getCoursesCompleted(
            @PathVariable Long personnelId) {
        try {
            List<CourseCompletedDto> data = personnelProfileService.getCoursesCompleted(personnelId);
            return ResponseEntity.ok(data != null ? data : Collections.emptyList());
        } catch (Exception e) {
            log.error("Error in getCoursesCompleted: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    /**
     * Get Decorations
     * GET /api/personnel/{personnelId}/decorations
     */
    @GetMapping("/{personnelId}/decorations")
    public ResponseEntity<List<DecorationDto>> getDecorations(
            @PathVariable Long personnelId) {
        try {
            List<DecorationDto> data = personnelProfileService.getDecorations(personnelId);
            return ResponseEntity.ok(data != null ? data : Collections.emptyList());
        } catch (Exception e) {
            log.error("Error in getDecorations: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    /**
     * Get Qualifications
     * GET /api/personnel/{personnelId}/qualifications
     */
    @GetMapping("/{personnelId}/qualifications")
    public ResponseEntity<List<QualificationDto>> getQualifications(
            @PathVariable Long personnelId) {
        try {
            List<QualificationDto> data = personnelProfileService.getQualifications(personnelId);
            return ResponseEntity.ok(data != null ? data : Collections.emptyList());
        } catch (Exception e) {
            log.error("Error in getQualifications: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    /**
     * Get Additional Qualifications
     * GET /api/personnel/{personnelId}/additional-qualifications
     */
    @GetMapping("/{personnelId}/additional-qualifications")
    public ResponseEntity<List<AdditionalQualificationDto>> getAdditionalQualifications(
            @PathVariable Long personnelId) {
        try {
            List<AdditionalQualificationDto> data = personnelProfileService
                    .getAdditionalQualifications(personnelId);
            return ResponseEntity.ok(data != null ? data : Collections.emptyList());
        } catch (Exception e) {
            log.error("Error in getAdditionalQualifications: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    /**
     * Get Family Members
     * GET /api/personnel/{personnelId}/family-members
     */
    @GetMapping("/{personnelId}/family-members")
    public ResponseEntity<List<FamilyMemberDto>> getFamilyMembers(
            @PathVariable Long personnelId) {
        try {
            List<FamilyMemberDto> data = personnelProfileService.getFamilyMembers(personnelId);
            return ResponseEntity.ok(data != null ? data : Collections.emptyList());
        } catch (Exception e) {
            log.error("Error in getFamilyMembers: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    /**
     * Get Discipline and Medical Details
     * GET /api/personnel/{personnelId}/discipline-medical
     */
    @GetMapping("/{personnelId}/discipline-medical")
    public ResponseEntity<DisciplineAndMedicalDto> getDisciplineAndMedical(
            @PathVariable Long personnelId) {
        try {
            DisciplineAndMedicalDto data = personnelProfileService
                    .getDisciplineAndMedical(personnelId);
            return ResponseEntity.ok(data != null ? data :
                    DisciplineAndMedicalDto.builder().build());
        } catch (Exception e) {
            log.error("Error in getDisciplineAndMedical: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
