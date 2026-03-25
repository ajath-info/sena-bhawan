package com.example.sena_bhawan.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCoursePanelBatchRequest {
    private Long scheduleId;
    private Long movementId;       // optional, can be null initially
    private String remarks;

    @NotEmpty
    private List<NominationItem> nominations;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class NominationItem {
        private Long personnelId;
        private String attendanceStatus; // "Detailed" or "Reserve"
        private Long serialNumber;       // nullable
    }
}