package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.BatchActionRequest;
import com.example.sena_bhawan.dto.BatchActionResponse;
import com.example.sena_bhawan.service.impl.BatchActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/batches")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BatchActionController {

    private final BatchActionService batchActionService;

    @PostMapping("/{batchId}/approve")
    public ResponseEntity<BatchActionResponse> approveBatch(
            @PathVariable Long batchId,
            @RequestBody BatchActionRequest request) {
        
        BatchActionResponse response = batchActionService.approveBatch(batchId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{batchId}/reject")
    public ResponseEntity<BatchActionResponse> rejectBatch(
            @PathVariable Long batchId,
            @RequestBody BatchActionRequest request) {
        
        BatchActionResponse response = batchActionService.rejectBatch(batchId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{batchId}/send-back")
    public ResponseEntity<BatchActionResponse> sendBackBatch(
            @PathVariable Long batchId,
            @RequestBody BatchActionRequest request) {
        
        BatchActionResponse response = batchActionService.sendBackBatch(batchId, request);
        return ResponseEntity.ok(response);
    }
}