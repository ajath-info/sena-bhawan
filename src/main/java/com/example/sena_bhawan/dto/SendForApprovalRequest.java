package com.example.sena_bhawan.dto;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class SendForApprovalRequest {
    private Long scheduleId;
    private String remarks;

    @NotEmpty
    private List<NominationSerialItem> nominations;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class NominationSerialItem {
        private Long personnelId;
        private Long serialNumber; // nullable
    }
}