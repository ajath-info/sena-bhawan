package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.CoursePanelRequest;
import com.example.sena_bhawan.dto.CoursePanelResponse;
import com.example.sena_bhawan.service.CoursePanelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
