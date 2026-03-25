package com.example.sena_bhawan.dto;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UpdateCoursePanelBatchRequest {
    private String status;   // optional — update batch status (DRAFT, PENDING_APPROVAL, etc.)
    private String remarks;  // optional — update batch remarks

    private List<NominationUpdate> nominations; // list of changes

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class NominationUpdate {
        private Long id;             // nomination id — required to identify which row to update
        private Long personnelId;    // use as fallback lookup if id is null
        private String attendanceStatus;
        private Long serialNumber;
        private String status;       // nomination-level status: ACTIVE, REMOVED, REPLACED, CONFIRMED
        private String remarks;
    }
}