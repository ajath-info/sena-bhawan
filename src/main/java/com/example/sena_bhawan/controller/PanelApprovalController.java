package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.PanelApprovalDTO;
import com.example.sena_bhawan.dto.RoleWiseApprovalResponse;
import com.example.sena_bhawan.service.PanelApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/panel-approval")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PanelApprovalController {
    
    private final PanelApprovalService panelApprovalService;
    
    @GetMapping("/role-wise")
    public ResponseEntity<RoleWiseApprovalResponse> getRoleWisePanels(
            @RequestParam Long movementId,
            @RequestParam String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        RoleWiseApprovalResponse response = panelApprovalService.getRoleWiseApprovalPanels(
                movementId, role, page, size);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{batchId}")
    public ResponseEntity<PanelApprovalDTO> getPanelDetails(@PathVariable Long batchId) {
        PanelApprovalDTO panel = panelApprovalService.getPanelDetails(batchId);
        return ResponseEntity.ok(panel);
    }
}