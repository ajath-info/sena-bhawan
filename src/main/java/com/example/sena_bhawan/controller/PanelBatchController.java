package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.PanelBatchListResponse;
import com.example.sena_bhawan.dto.PersonnelDataDTO;
import com.example.sena_bhawan.service.CoursePanelBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/panel-batches")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PanelBatchController {

    private final CoursePanelBatchService panelBatchService;

    @GetMapping("/movement/{movementId}/status/{status}")
    public ResponseEntity<PanelBatchListResponse> getPanelBatches(
            @PathVariable Long movementId,
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PanelBatchListResponse response = panelBatchService.getPanelBatchesByMovementAndStatus(
                movementId, status, page, size);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/movement/{movementId}/status/{status}/count")
    public ResponseEntity<Long> getPanelCount(
            @PathVariable Long movementId,
            @PathVariable String status) {
        
        long count = panelBatchService.getCountByMovementAndStatus(movementId, status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{batchId}/personnel")
    public ResponseEntity<List<PersonnelDataDTO>> getPersonnelByBatchId(@PathVariable Long batchId) {
        List<PersonnelDataDTO> personnel = panelBatchService.getPersonnelByBatchId(batchId);
        return ResponseEntity.ok(personnel);
    }

    // New endpoint without movement filter (for course approval status page)
    @GetMapping("/status/{status}")
    public ResponseEntity<PanelBatchListResponse> getAllPanelBatchesByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PanelBatchListResponse response = panelBatchService.getAllPanelBatchesByStatus(
                status, page, size);
        return ResponseEntity.ok(response);
    }
}