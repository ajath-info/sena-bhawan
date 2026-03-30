package com.example.sena_bhawan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleWiseApprovalResponse {
    private List<PanelApprovalDTO> pendingPanels;
    private List<PanelApprovalDTO> approvedPanels;
    private List<PanelApprovalDTO> rejectedPanels;
    private PaginationInfo pagination;
}