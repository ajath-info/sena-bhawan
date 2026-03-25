package com.example.sena_bhawan.dto;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;


@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CoursePanelBatchResponse {
    private Long batchId;
    private Long scheduleId;
    private String status;
    private Integer totalNominations;
    private String message;
    private List<NominationSummary> nominations;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class NominationSummary {
        private Long nominationId;
        private Long personnelId;
        private String attendanceStatus;
        private Long serialNumber;
        private String status;
    }
}