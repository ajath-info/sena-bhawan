package com.example.sena_bhawan.dto;

import lombok.Data;
import java.util.List;

@Data
public class CoursePanelStatusUpdateRequest {
    private Long scheduleId;
    private List<StatusUpdate> statusUpdates;
    
    @Data
    public static class StatusUpdate {
        private Long personnelId;
        private String attendanceStatus; // "Retain" or "Reserve"
    }
}