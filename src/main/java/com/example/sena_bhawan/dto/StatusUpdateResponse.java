package com.example.sena_bhawan.dto;

import java.util.List;

// Response DTO for status update
public class StatusUpdateResponse {
    private String message;
    private int updatedCount;
    private List<String> errors;
    
    public StatusUpdateResponse(String message, int updatedCount, List<String> errors) {
        this.message = message;
        this.updatedCount = updatedCount;
        this.errors = errors;
    }
    
    // Getters and setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public int getUpdatedCount() { return updatedCount; }
    public void setUpdatedCount(int updatedCount) { this.updatedCount = updatedCount; }
    
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}