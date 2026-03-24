package com.example.sena_bhawan.controller;

import com.example.sena_bhawan.dto.GradeUpdateRequestDto;
import com.example.sena_bhawan.dto.GradeUpdateResponseDto;
import com.example.sena_bhawan.service.GradeUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/grade-update")
@RequiredArgsConstructor
public class GradeUpdateController {

    private final GradeUpdateService gradeUpdateService;

    @GetMapping("/{scheduleId}")
    public ResponseEntity<GradeUpdateResponseDto> getGradeUpdateData(@PathVariable Long scheduleId) {
        log.info("GET request to fetch grade update data for schedule: {}", scheduleId);
        GradeUpdateResponseDto response = gradeUpdateService.getGradeUpdateData(scheduleId);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/{scheduleId}")
    public ResponseEntity<Map<String, String>> saveGrades(@PathVariable Long scheduleId,
                                                          @RequestBody GradeUpdateRequestDto request) {
        log.info("POST request to save grades for schedule: {}, updates: {}",
                scheduleId, request.getGradeUpdates().size());

        request.setScheduleId(scheduleId);
        gradeUpdateService.saveGrades(request);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Grades saved successfully");
        response.put("scheduleId", scheduleId.toString());
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }

    /**
     * Export Panel Data to PDF
     * GET /api/grade-update/{scheduleId}/export/pdf
     */
    @GetMapping(value = "/{scheduleId}/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportToPdf(@PathVariable Long scheduleId) {
        log.info("Exporting panel data to PDF for schedule: {}", scheduleId);

        byte[] pdfBytes = gradeUpdateService.exportToPdf(scheduleId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "panel_report_" + scheduleId + ".pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    /**
     * Export Panel Data to Excel
     * GET /api/grade-update/{scheduleId}/export/excel
     */
    @GetMapping(value = "/{scheduleId}/export/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportToExcel(@PathVariable Long scheduleId) {
        log.info("Exporting panel data to Excel for schedule: {}", scheduleId);

        byte[] excelBytes = gradeUpdateService.exportToExcel(scheduleId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "panel_report_" + scheduleId + ".xlsx");
        headers.setContentLength(excelBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }
}