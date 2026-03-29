package com.example.sena_bhawan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PanelBatchListResponse {
    private List<PanelBatchListDTO> data;
    private long totalCount;
    private int currentPage;
    private int pageSize;
    private int totalPages;
}