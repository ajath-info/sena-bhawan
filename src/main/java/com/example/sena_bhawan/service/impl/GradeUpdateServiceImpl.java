package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.*;
import com.example.sena_bhawan.entity.*;
import com.example.sena_bhawan.repository.*;
import com.example.sena_bhawan.service.GradeUpdateService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import com.lowagie.text.*;
import org.apache.poi.ss.usermodel.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradeUpdateServiceImpl implements GradeUpdateService {

    private final CourseScheduleRepository courseScheduleRepository;
    private final CoursePanelRepository nominationRepository;
    private final PersonnelRepository personnelRepository;
    private final PostingDetailsRepository postingDetailsRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

    /**
     * GET API: Fetch grade update data for a specific schedule
     */
    @Override
    @Transactional(readOnly = true)
    public GradeUpdateResponseDto getGradeUpdateData(Long scheduleId) {
        log.info("Fetching grade update data for schedule ID: {}", scheduleId);

        try {
            // 1. Fetch course schedule with course details
            CourseSchedule schedule = courseScheduleRepository.findByIdWithCourse(scheduleId)
                    .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + scheduleId));

            CourseMaster course = schedule.getCourse();

            // 2. Fetch all nominations for this schedule
            List<CoursePanelNomination> nominations = nominationRepository.findByScheduleId(scheduleId);

            // 3. Build base response with course info
            GradeUpdateResponseDto response = new GradeUpdateResponseDto();
            response.setCourseInfo(buildCourseInfo(course, schedule, nominations.size()));

            // 4. Handle empty nominations gracefully
            if (nominations.isEmpty()) {
                log.warn("No nominations found for schedule ID: {}, returning empty participants list", scheduleId);
                response.setParticipants(new ArrayList<>());
                return response;
            }

            // 5. Get personnel IDs from nominations
            List<Long> personnelIds = nominations.stream()
                    .map(CoursePanelNomination::getPersonnelId)
                    .collect(Collectors.toList());

            // 6. Fetch personnel details
            List<Personnel> personnelList = personnelRepository.findAllByIdIn(personnelIds);
            Map<Long, Personnel> personnelMap = personnelList.stream()
                    .collect(Collectors.toMap(Personnel::getId, p -> p));

            // 7. Fetch current posting for each personnel
            Map<Long, PostingDetails> currentPostingMap = getCurrentPostings(personnelIds);

            // 8. Build participants list
            response.setParticipants(buildParticipantsList(nominations, personnelMap, currentPostingMap));

            log.info("Successfully fetched grade update data for schedule ID: {}, participants: {}",
                    scheduleId, response.getParticipants().size());

            return response;

        } catch (Exception e) {
            log.error("Error fetching grade update data for schedule ID: {}", scheduleId, e);
            throw new RuntimeException("Failed to fetch grade update data: " + e.getMessage(), e);
        }
    }

    /**
     * POST/PUT API: Save grades to nomination table (UPSERT)
     */
    @Override
    @Transactional
    public void saveGrades(GradeUpdateRequestDto request) {
        Long scheduleId = request.getScheduleId();
        log.info("Saving grades for schedule ID: {}, updates count: {}", scheduleId, request.getGradeUpdates().size());

        try {
            // Validate schedule exists
            if (!courseScheduleRepository.existsById(scheduleId)) {
                throw new RuntimeException("Schedule not found with id: " + scheduleId);
            }

            int updatedCount = 0;
            int createdCount = 0;

            for (ParticipantGradeUpdateDto update : request.getGradeUpdates()) {
                try {
                    String gradeStatus = determineGradeStatus(update.getGrade());

                    // Try to update existing record first
                    int updated = nominationRepository.updateGradeDetails(
                            scheduleId,
                            update.getPersonnelId(),
                            update.getGrade(),
                            update.getInstructorAward(),
                            update.getRemarks(),
                            gradeStatus
                    );

                    if (updated == 0) {
                        // If no record updated, create new one
                        createNewNomination(scheduleId, update, gradeStatus);
                        createdCount++;
                    } else {
                        updatedCount++;
                    }
                } catch (Exception e) {
                    log.error("Error processing grade update for personnelId: {}", update.getPersonnelId(), e);
                    throw new RuntimeException("Failed to save grade for personnel: " + update.getPersonnelId() +
                            " - " + e.getMessage(), e);
                }
            }

            log.info("Grades saved successfully for schedule ID: {}, updated: {}, created: {}",
                    scheduleId, updatedCount, createdCount);

        } catch (Exception e) {
            log.error("Error saving grades for schedule ID: {}", scheduleId, e);
            throw new RuntimeException("Failed to save grades: " + e.getMessage(), e);
        }
    }

    // ==================== Private Helper Methods ====================

    /**
     * Fetch current postings for all personnel IDs
     */
    private Map<Long, PostingDetails> getCurrentPostings(List<Long> personnelIds) {
        Map<Long, PostingDetails> currentPostingMap = new HashMap<>();

        for (Long personnelId : personnelIds) {
            try {
                Optional<PostingDetails> currentPosting = postingDetailsRepository.findActivePostingByPersonnelId(personnelId);
                currentPostingMap.put(personnelId, currentPosting.orElse(null));
            } catch (Exception e) {
                log.error("Error fetching posting for personnel ID: {}", personnelId, e);
                currentPostingMap.put(personnelId, null);
            }
        }
        return currentPostingMap;
    }

    /**
     * Build CourseInfoDto from course and schedule
     */
    private CourseInfoDto buildCourseInfo(CourseMaster course, CourseSchedule schedule, int totalParticipants) {
        try {
            CourseInfoDto courseInfo = new CourseInfoDto();
            courseInfo.setCourseName(course.getCourseName());
            courseInfo.setCourseCode(generateCourseCode(course, schedule));
            courseInfo.setStartDate(schedule.getStartDate().format(DATE_FORMATTER));
            courseInfo.setEndDate(schedule.getEndDate().format(DATE_FORMATTER));
            courseInfo.setLocation(schedule.getVenue() != null ? schedule.getVenue() : course.getLocation());
            courseInfo.setTotalParticipants(totalParticipants);
            courseInfo.setCompletionDate(schedule.getEndDate().format(DATE_FORMATTER));
            courseInfo.setStatus("Completed");
            return courseInfo;
        } catch (Exception e) {
            log.error("Error building course info", e);
            throw new RuntimeException("Failed to build course information: " + e.getMessage(), e);
        }
    }

    /**
     * Generate course code from course name and schedule
     */
    private String generateCourseCode(CourseMaster course, CourseSchedule schedule) {
        try {
            String prefix = course.getCourseName()
                    .replace("Course", "")
                    .replace("Warfare", "W")
                    .trim()
                    .toUpperCase()
                    .substring(0, Math.min(3, course.getCourseName().length()));

            String year = schedule.getYear() != null ? schedule.getYear() : "NA";
            String batch = schedule.getBatchNumber() != null ? schedule.getBatchNumber() : "NA";

            return prefix + "-" + year + "-" + batch;
        } catch (Exception e) {
            log.error("Error generating course code", e);
            return "ERR-NA-NA";
        }
    }

    /**
     * Build list of ParticipantGradeDto from nominations
     */
    private List<ParticipantGradeDto> buildParticipantsList(
            List<CoursePanelNomination> nominations,
            Map<Long, Personnel> personnelMap,
            Map<Long, PostingDetails> currentPostingMap) {

        List<ParticipantGradeDto> participants = new ArrayList<>();
        int srNo = 1;

        for (CoursePanelNomination nomination : nominations) {
            try {
                Personnel personnel = personnelMap.get(nomination.getPersonnelId());
                if (personnel == null) {
                    log.warn("Personnel not found for ID: {}", nomination.getPersonnelId());
                    continue;
                }

                PostingDetails currentPosting = currentPostingMap.get(nomination.getPersonnelId());

                // Determine grade status with fallback
                String gradeStatus = nomination.getGradeStatus();
                if (gradeStatus == null) {
                    gradeStatus = (nomination.getGrade() != null && !nomination.getGrade().isEmpty()) ? "Graded" : "Pending";
                }

                ParticipantGradeDto participant = new ParticipantGradeDto();
                participant.setSrNo(srNo++);
                participant.setPersonnelId(nomination.getPersonnelId());
                participant.setOfficerName(personnel.getFullName());
                participant.setArmyNo(personnel.getArmyNo());
                participant.setUnit(currentPosting != null ? currentPosting.getUnitName() : "N/A");
                participant.setPanelPosition(nomination.getAttendanceStatus());
                participant.setGrade(nomination.getGrade() != null ? nomination.getGrade() : "");
                participant.setInstructorAward(nomination.getInstructorAward() != null ? nomination.getInstructorAward() : false);
                participant.setRemarks(nomination.getGradeRemarks() != null ? nomination.getGradeRemarks() : "");
                participant.setGradeStatus(gradeStatus);

                participants.add(participant);

            } catch (Exception e) {
                log.error("Error building participant for nomination: {}", nomination.getId(), e);
                // Continue with next participant
            }
        }

        return participants;
    }

    /**
     * Determine grade status based on grade value
     */
    private String determineGradeStatus(String grade) {
        try {
            return (grade != null && !grade.trim().isEmpty()) ? "Graded" : "Pending";
        } catch (Exception e) {
            log.error("Error determining grade status for grade: {}", grade, e);
            return "Pending";
        }
    }

    /**
     * Create new nomination record (fallback when update doesn't affect any rows)
     */
    private void createNewNomination(Long scheduleId, ParticipantGradeUpdateDto update, String gradeStatus) {
        try {
            log.info("Creating new nomination record for schedule: {}, personnel: {}",
                    scheduleId, update.getPersonnelId());

            CoursePanelNomination nomination = CoursePanelNomination.builder()
                    .scheduleId(scheduleId)
                    .personnelId(update.getPersonnelId())
                    .attendanceStatus("Reserve")
                    .grade(update.getGrade())
                    .instructorAward(update.getInstructorAward())
                    .gradeRemarks(update.getRemarks())
                    .gradeStatus(gradeStatus)
                    .build();

            nominationRepository.save(nomination);

        } catch (Exception e) {
            log.error("Error creating new nomination for schedule: {}, personnel: {}",
                    scheduleId, update.getPersonnelId(), e);
            throw new RuntimeException("Failed to create nomination record: " + e.getMessage(), e);
        }
    }

    /**
     * Export Panel Data to PDF using OpenPDF
     */
    @Override
    public byte[] exportToPdf(Long scheduleId) {
        try {
            // Fetch data
            GradeUpdateResponseDto data = getGradeUpdateData(scheduleId);
            CourseInfoDto courseInfo = data.getCourseInfo();
            List<ParticipantGradeDto> participants = data.getParticipants();

            // Create PDF document
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();

            // Add Title - Using com.lowagie.text.Font
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(27, 67, 50));
            Paragraph title = new Paragraph("Course Completion Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Add Course Info - Using com.lowagie.text.Font
            Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(27, 67, 50));
            Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL);

            Paragraph courseInfoPara = new Paragraph();
            courseInfoPara.add(new Chunk("Course Name: ", headerFont));
            courseInfoPara.add(new Chunk(courseInfo.getCourseName() + "\n", normalFont));
            courseInfoPara.add(new Chunk("Course Code: ", headerFont));
            courseInfoPara.add(new Chunk(courseInfo.getCourseCode() + "\n", normalFont));
            courseInfoPara.add(new Chunk("Duration: ", headerFont));
            courseInfoPara.add(new Chunk(courseInfo.getStartDate() + " to " + courseInfo.getEndDate() + "\n", normalFont));
            courseInfoPara.add(new Chunk("Location: ", headerFont));
            courseInfoPara.add(new Chunk(courseInfo.getLocation() + "\n", normalFont));
            courseInfoPara.add(new Chunk("Total Participants: ", headerFont));
            courseInfoPara.add(new Chunk(String.valueOf(courseInfo.getTotalParticipants()) + "\n", normalFont));
            courseInfoPara.add(new Chunk("Completion Date: ", headerFont));
            courseInfoPara.add(new Chunk(courseInfo.getCompletionDate() + "\n", normalFont));
            courseInfoPara.setSpacingAfter(20);
            document.add(courseInfoPara);

            // Add Generation Date
            Paragraph genDate = new Paragraph("Generated on: " + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")), normalFont);
            genDate.setSpacingAfter(20);
            document.add(genDate);

            // Create Table
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setWidths(new float[]{5, 20, 15, 15, 15, 12, 20});

            // Table Header - Using com.lowagie.text.Font
            Font headerCellFont = new Font(Font.HELVETICA, 10, Font.BOLD, new Color(255, 255, 255));
            addPdfCell(table, "Sr No", headerCellFont, new Color(27, 67, 50));
            addPdfCell(table, "Officer Name", headerCellFont, new Color(27, 67, 50));
            addPdfCell(table, "Army No", headerCellFont, new Color(27, 67, 50));
            addPdfCell(table, "Unit", headerCellFont, new Color(27, 67, 50));
            addPdfCell(table, "Panel Position", headerCellFont, new Color(27, 67, 50));
            addPdfCell(table, "Final Grade", headerCellFont, new Color(27, 67, 50));
            addPdfCell(table, "Remarks", headerCellFont, new Color(27, 67, 50));

            // Table Body - Using com.lowagie.text.Font
            Font bodyFont = new Font(Font.HELVETICA, 9, Font.NORMAL);
            int srNo = 1;
            for (ParticipantGradeDto p : participants) {
                addPdfCell(table, String.valueOf(srNo++), bodyFont, null);
                addPdfCell(table, p.getOfficerName(), bodyFont, null);
                addPdfCell(table, p.getArmyNo(), bodyFont, null);
                addPdfCell(table, p.getUnit(), bodyFont, null);
                addPdfCell(table, p.getPanelPosition(), bodyFont, null);
                String grade = p.getGrade() != null && !p.getGrade().isEmpty() ? p.getGrade() : "Pending";
                addPdfCell(table, grade, bodyFont, null);
                addPdfCell(table, p.getRemarks() != null ? p.getRemarks() : "-", bodyFont, null);
            }

            document.add(table);

            // Add Footer - Using com.lowagie.text.Font
            Font footerFont = new Font(Font.HELVETICA, 8, Font.ITALIC, new Color(100, 100, 100));
            Paragraph footer = new Paragraph("This is a system-generated report. ARMY HRMS", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(20);
            document.add(footer);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            log.error("Error generating PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    private void addPdfCell(PdfPTable table, String text, Font font, Color backgroundColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        if (backgroundColor != null) {
            cell.setBackgroundColor(backgroundColor);
        }
        table.addCell(cell);
    }

    /**
     * Export Panel Data to Excel using Apache POI
     */
    @Override
    public byte[] exportToExcel(Long scheduleId) {
        try {
            // Fetch data
            GradeUpdateResponseDto data = getGradeUpdateData(scheduleId);
            CourseInfoDto courseInfo = data.getCourseInfo();
            List<ParticipantGradeDto> participants = data.getParticipants();

            // Create Workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Panel Report");

            // Create Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Create Body Style
            CellStyle bodyStyle = workbook.createCellStyle();
            bodyStyle.setBorderBottom(BorderStyle.THIN);
            bodyStyle.setBorderTop(BorderStyle.THIN);
            bodyStyle.setBorderLeft(BorderStyle.THIN);
            bodyStyle.setBorderRight(BorderStyle.THIN);

            // Create Title Row
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Course Completion Report");
            CellStyle titleStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 6));

            // Course Info Rows
            int rowNum = 2;
            rowNum = addExcelInfoRow(sheet, rowNum, "Course Name:", courseInfo.getCourseName(), bodyStyle, workbook);
            rowNum = addExcelInfoRow(sheet, rowNum, "Course Code:", courseInfo.getCourseCode(), bodyStyle, workbook);
            rowNum = addExcelInfoRow(sheet, rowNum, "Duration:", courseInfo.getStartDate() + " to " + courseInfo.getEndDate(), bodyStyle, workbook);
            rowNum = addExcelInfoRow(sheet, rowNum, "Location:", courseInfo.getLocation(), bodyStyle, workbook);
            rowNum = addExcelInfoRow(sheet, rowNum, "Total Participants:", String.valueOf(courseInfo.getTotalParticipants()), bodyStyle, workbook);
            rowNum = addExcelInfoRow(sheet, rowNum, "Completion Date:", courseInfo.getCompletionDate(), bodyStyle, workbook);
            rowNum = addExcelInfoRow(sheet, rowNum, "Generated on:", java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")), bodyStyle, workbook);

            rowNum++; // Blank row

            // Create Header Row
            Row headerRow = sheet.createRow(rowNum);
            String[] headers = {"Sr No", "Officer Name", "Army No", "Unit", "Panel Position", "Final Grade", "Remarks"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, headers[i].length() * 400);
            }

            // Data Rows
            int srNo = 1;
            for (ParticipantGradeDto p : participants) {
                rowNum++;
                Row dataRow = sheet.createRow(rowNum);
                dataRow.createCell(0).setCellValue(srNo++);
                dataRow.createCell(1).setCellValue(p.getOfficerName());
                dataRow.createCell(2).setCellValue(p.getArmyNo());
                dataRow.createCell(3).setCellValue(p.getUnit());
                dataRow.createCell(4).setCellValue(p.getPanelPosition());
                String grade = p.getGrade() != null && !p.getGrade().isEmpty() ? p.getGrade() : "Pending";
                dataRow.createCell(5).setCellValue(grade);
                dataRow.createCell(6).setCellValue(p.getRemarks() != null ? p.getRemarks() : "-");

                // Apply style to all cells
                for (int i = 0; i < 7; i++) {
                    dataRow.getCell(i).setCellStyle(bodyStyle);
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.close();
            return out.toByteArray();

        } catch (Exception e) {
            log.error("Error generating Excel: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }

    private int addExcelInfoRow(Sheet sheet, int rowNum, String label, String value, CellStyle style, Workbook workbook) {
        Row row = sheet.createRow(rowNum);

        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(style);

        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value);
        valueCell.setCellStyle(style);

        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowNum, rowNum, 1, 6));
        return rowNum + 1;
    }


}