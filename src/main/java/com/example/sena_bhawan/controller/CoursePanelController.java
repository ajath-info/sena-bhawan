package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.CoursePanelRequest;
import com.example.sena_bhawan.dto.CoursePanelResponse;
import com.example.sena_bhawan.dto.OfficerStatusResponse;
import com.example.sena_bhawan.dto.OngoingCoursesResponse;
import com.example.sena_bhawan.service.CoursePanelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/course-panel")
@RequiredArgsConstructor
@CrossOrigin
public class CoursePanelController {

    private final CoursePanelService service;

    /* ===============================
       STEP-3 SAVE
    ================================ */
    @PostMapping("/nomination")
    public ResponseEntity<Void> savePanel(
            @RequestBody CoursePanelRequest request) {

        service.savePanel(request);
        return ResponseEntity.ok().build();
    }

    /* ===============================
       STEP-4 LOAD
    ================================ */
    @GetMapping("/nomination/{scheduleId}")
    public List<CoursePanelResponse> getPanel(
            @PathVariable Long scheduleId) {

        return service.getPanel(scheduleId);
    }

    @GetMapping("/ongoing-strength")
    public ResponseEntity<?> getOngoingStrength() {
        Map<String, Map<String, Long>> data = service.getOngoingCoursesByStatus();

        // Build frontend-friendly response
        List<String> labels = new ArrayList<>(data.keySet());
        List<Long> confirmed = new ArrayList<>();
        List<Long> pending   = new ArrayList<>();
        List<Long> rejected  = new ArrayList<>();

        for (String course : labels) {
            Map<String, Long> statusMap = data.get(course);
            confirmed.add(statusMap.getOrDefault("CONFIRMED",         0L));
            pending  .add(statusMap.getOrDefault("PENDING_APPROVAL",  0L));
            rejected .add(statusMap.getOrDefault("REJECTED",          0L));
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("labels",    labels);
        response.put("confirmed", confirmed);
        response.put("pending",   pending);
        response.put("rejected",  rejected);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/officer-status")
    public ResponseEntity<OfficerStatusResponse> getOfficerStatusOverview() {
        return ResponseEntity.ok(service.getOfficerStatusOverview());
    }
}
