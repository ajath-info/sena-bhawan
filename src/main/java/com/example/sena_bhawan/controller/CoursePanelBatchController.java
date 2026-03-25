package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.CoursePanelBatchResponse;
import com.example.sena_bhawan.dto.CreateCoursePanelBatchRequest;
import com.example.sena_bhawan.dto.UpdateCoursePanelBatchRequest;
import com.example.sena_bhawan.service.CoursePanelBatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/course-panel/batch")
@RequiredArgsConstructor
public class CoursePanelBatchController {

    private final CoursePanelBatchService batchService;

    /**
     * POST /api/course-panel/batch
     * Creates a new batch and saves all nominations inside it.
     *
     * Body example:
     * {
     *   "scheduleId": 5,
     *   "movementId": null,
     *   "remarks": "Initial panel for DSSC",
     *   "nominations": [
     *     { "personnelId": 101, "attendanceStatus": "Detailed", "serialNumber": 1 },
     *     { "personnelId": 102, "attendanceStatus": "Reserve",  "serialNumber": null }
     *   ]
     * }
     */
    @PostMapping
    public ResponseEntity<CoursePanelBatchResponse> createBatch(
            @RequestBody @Valid CreateCoursePanelBatchRequest request) {
        CoursePanelBatchResponse response = batchService.createBatch(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/course-panel/batch/{batchId}
     * Modify batch status/remarks and update any subset of its nominations.
     *
     * Body example (all fields optional):
     * {
     *   "status": "PENDING_APPROVAL",
     *   "remarks": "Forwarded for CO approval",
     *   "nominations": [
     *     { "id": 55, "attendanceStatus": "Detailed", "serialNumber": 3 },
     *     { "personnelId": 107, "status": "REMOVED" }
     *   ]
     * }
     */
    @PutMapping("/{batchId}")
    public ResponseEntity<CoursePanelBatchResponse> updateBatch(
            @PathVariable Long batchId,
            @RequestBody UpdateCoursePanelBatchRequest request) {
        CoursePanelBatchResponse response = batchService.updateBatch(batchId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/course-panel/batch/{batchId}
     * Fetch a batch with all its nominations.
     */
    @GetMapping("/{batchId}")
    public ResponseEntity<CoursePanelBatchResponse> getBatch(@PathVariable Long batchId) {
        return ResponseEntity.ok(batchService.getBatch(batchId));
    }
}