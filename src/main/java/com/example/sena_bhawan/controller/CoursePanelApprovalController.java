package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.SendForApprovalRequest;
import com.example.sena_bhawan.dto.SendForApprovalResponse;
import com.example.sena_bhawan.service.SendForApprovalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/course-panel")
@RequiredArgsConstructor
public class CoursePanelApprovalController {

    private final SendForApprovalService sendForApprovalService;

    /**
     * POST /api/course-panel/send-for-approval
     *
     * Creates CoursePanelBatch (movementId=1, status=PENDING_APPROVAL)
     * and updates all CoursePanelNomination rows for the schedule
     * with their serialNumbers and the new batchId — in a single transaction.
     *
     * Body:
     * {
     *   "scheduleId": 5,
     *   "remarks": "Forwarded for approval",
     *   "nominations": [
     *     { "personnelId": 101, "serialNumber": 1 },
     *     { "personnelId": 102, "serialNumber": 2 },
     *     { "personnelId": 103, "serialNumber": null }
     *   ]
     * }
     */
    @PostMapping("/send-for-approval")
    public ResponseEntity<SendForApprovalResponse> sendForApproval(
            @RequestBody @Valid SendForApprovalRequest request) {
        SendForApprovalResponse response = sendForApprovalService.sendForApproval(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}