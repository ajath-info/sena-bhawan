package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.CoursePanelStatusUpdateRequest;
import com.example.sena_bhawan.dto.StatusUpdateResponse;
import com.example.sena_bhawan.entity.CoursePanelNomination;
import com.example.sena_bhawan.repository.CoursePanelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            
            for (CoursePanelStatusUpdateRequest.StatusUpdate update : updates) {
                Long personnelId = update.getPersonnelId();
                String newStatus = update.getAttendanceStatus();
                
                // Validate status
                if (!newStatus.equalsIgnoreCase("Detailed") && !newStatus.equalsIgnoreCase("Reserve")) {
                    errors.add("Invalid status for personnelId " + personnelId + ": " + newStatus);
                    continue;
                }
                
                // Find the nomination
                Optional<CoursePanelNomination> optionalNomination = 
                    coursePanelRepository.findByScheduleIdAndPersonnelId(scheduleId, personnelId);
                
                if (optionalNomination.isPresent()) {
                    CoursePanelNomination nomination = optionalNomination.get();
                    nomination.setAttendanceStatus(newStatus);
                    updatedNominations.add(coursePanelRepository.save(nomination));
                } else {
                    errors.add("Nomination not found for scheduleId: " + scheduleId + ", personnelId: " + personnelId);
                }
            }
            
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new StatusUpdateResponse(
                    "Partial success", updatedNominations.size(), errors));
            }
            
            return ResponseEntity.ok(new StatusUpdateResponse(
                "All statuses updated successfully", updatedNominations.size(), errors));
            
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
