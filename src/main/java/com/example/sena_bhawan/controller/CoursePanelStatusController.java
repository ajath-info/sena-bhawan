package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.CoursePanelStatusUpdateRequest;
import com.example.sena_bhawan.dto.StatusUpdateResponse;
import com.example.sena_bhawan.entity.CoursePanelNomination;
import com.example.sena_bhawan.repository.CoursePanelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/course-panel")
@CrossOrigin(origins = "*")
public class CoursePanelStatusController {

    @Autowired
    private CoursePanelRepository coursePanelRepository;

    /**
     * Update attendance status for multiple personnel
     * Endpoint: PUT /api/course-panel/status
     */
    @PutMapping("/status")
    public ResponseEntity<?> updateAttendanceStatus(@RequestBody CoursePanelStatusUpdateRequest request) {
        try {
            Long scheduleId = request.getScheduleId();
            List<CoursePanelStatusUpdateRequest.StatusUpdate> updates = request.getStatusUpdates();

            List<CoursePanelNomination> updatedNominations = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            int createdCount = 0;

            for (CoursePanelStatusUpdateRequest.StatusUpdate update : updates) {
                Long personnelId = update.getPersonnelId();
                String newStatus = update.getAttendanceStatus();

                // Validate status
                if (!newStatus.equalsIgnoreCase("Detailed") && !newStatus.equalsIgnoreCase("Reserve")) {
                    errors.add("Invalid status for personnelId " + personnelId + ": " + newStatus);
                    continue;
                }

                // Find existing or create new
                Optional<CoursePanelNomination> optionalNomination =
                        coursePanelRepository.findByScheduleIdAndPersonnelId(scheduleId, personnelId);

                CoursePanelNomination nomination;

                if (optionalNomination.isPresent()) {
                    // UPDATE existing
                    nomination = optionalNomination.get();
                    nomination.setAttendanceStatus(newStatus);
                    nomination.setUpdatedAt(LocalDateTime.now());
                } else {
                    // CREATE new nomination
                    nomination = CoursePanelNomination.builder()
                            .scheduleId(scheduleId)
                            .personnelId(personnelId)
                            .attendanceStatus(newStatus)
                            .status("ACTIVE")
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    createdCount++;
                }

                updatedNominations.add(coursePanelRepository.save(nomination));
            }

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new StatusUpdateResponse(
                        "Partial success", updatedNominations.size(), errors));
            }

            String message = createdCount > 0
                    ? "Updated " + (updatedNominations.size() - createdCount) + ", created " + createdCount
                    : "All statuses updated successfully";

            return ResponseEntity.ok(new StatusUpdateResponse(message, updatedNominations.size(), errors));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating status: " + e.getMessage());
        }
    }
    
    /**
     * Update single personnel status
     * Endpoint: PUT /api/course-panel/status/{scheduleId}/{personnelId}
     */
    @PutMapping("/status/{scheduleId}/{personnelId}")
    public ResponseEntity<?> updateSingleStatus(
            @PathVariable Long scheduleId,
            @PathVariable Long personnelId,
            @RequestParam String status) {
        
        try {
            // Validate status
            if (!status.equalsIgnoreCase("Detailed") && !status.equalsIgnoreCase("Reserve")) {
                return ResponseEntity.badRequest().body("Invalid status. Must be 'Detailed' or 'Reserve'");
            }
            
            // Find the nomination
            Optional<CoursePanelNomination> optionalNomination = 
                coursePanelRepository.findByScheduleIdAndPersonnelId(scheduleId, personnelId);
            
            if (optionalNomination.isPresent()) {
                CoursePanelNomination nomination = optionalNomination.get();
                nomination.setAttendanceStatus(status);
                CoursePanelNomination updated = coursePanelRepository.save(nomination);
                return ResponseEntity.ok(updated);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating status: " + e.getMessage());
        }
    }
    
    /**
     * Get all nominations for a schedule
     * Endpoint: GET /api/course-panel/nominations/{scheduleId}
     */
    @GetMapping("/nominations/{scheduleId}")
    public ResponseEntity<?> getNominationsBySchedule(@PathVariable Long scheduleId) {
        try {
            List<CoursePanelNomination> nominations = coursePanelRepository.findByScheduleId(scheduleId);
            return ResponseEntity.ok(nominations);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching nominations: " + e.getMessage());
        }
    }
}
