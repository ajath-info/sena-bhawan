package com.example.sena_bhawan.dto;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SendForApprovalResponse {
    private Long batchId;
    private Long scheduleId;
    private Long movementId;
    private String status;
    private Integer totalNominations;
    private String message;
}