package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.AttendanceRequest;
import com.example.sena_bhawan.dto.AttendanceResponse;
import com.example.sena_bhawan.dto.PanelOfficerDTO;
import com.example.sena_bhawan.entity.PanelAttendance;
import com.example.sena_bhawan.entity.PostingDetails;
import com.example.sena_bhawan.repository.PanelAttendanceRepository;
import com.example.sena_bhawan.repository.PersonnelRepository;
import com.example.sena_bhawan.repository.PostingDetailsRepository;
import com.example.sena_bhawan.service.PanelSelectionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class PanelSelectionServiceImpl implements PanelSelectionService {

    private final PersonnelRepository personnelRepository;
    private final PostingDetailsRepository postingDetailsRepository;
    private final PanelAttendanceRepository attendanceRepository;

    @Override
    public List<PanelOfficerDTO> getEligibleOfficers() {
        return List.of();
    }

//    @Override
//    public Page<PanelOfficerDTO> getOfficers(int page, int size) {
//
//        Pageable pageable = (Pageable) PageRequest.of(page, size);
//        return personnelRepository.findAll((org.springframework.data.domain.Pageable) pageable)
//                .map(personnel -> {
//                    PostingDetails posting =
//                            postingDetailsRepository
//                                    .findTopByPersonnelIdOrderByFromDateDesc(personnel.getId())
//                                    .orElse(null);
//
//                    return new PanelOfficerDTO(
//                            personnel.getId(),
//                            personnel.getFullName(),
//                            personnel.getArmyNo(),
//                            personnel.getRank(),
//                            posting != null ? posting.getUnitName() : "NA",
//                            posting != null ? posting.getCommand() : "NA",
//                            personnel.getDateOfSeniority()
//                    );
//                });
//    }
@Override
public List<PanelOfficerDTO> getOfficers() {

    return personnelRepository.findAll()
            .stream()
            .map(personnel -> {

                PostingDetails posting =
                        (PostingDetails) postingDetailsRepository
                                .findTopByPersonnelIdOrderByFromDateDesc(personnel.getId())
                                .orElse(null);

                return new PanelOfficerDTO(
                        personnel.getId(),

                        personnel.getFullName(),
                        personnel.getArmyNo(),
                        personnel.getRank(),

                        posting != null ? posting.getUnitName() : "NA",
                        posting != null ? posting.getCommand() : "NA",

                        personnel.getDateOfCommission(),
                        personnel.getDateOfSeniority(),
                        personnel.getDateOfBirth(),

                        personnel.getReligion(),
                        personnel.getMaritalStatus(),
                        personnel.getMedicalCategory(),
                        personnel.getMedicalRemark(),   



                        personnel.getMobileNumber(),
                        personnel.getEmailAddress(),

                        personnel.getCity(),
                        personnel.getState(),

                        personnel.getOfficerImage()
                );
            })
            .toList();
}




    @Override
    @Transactional
    public void updateAttendanceBulk(List<AttendanceRequest> requests) {

        for (AttendanceRequest req : requests) {
            updateAttendance(
                    req.getCourseId(),
                    req.getScheduleId(),
                    req.getPersonnelId(),
                    req.getStatus()
            );
        }
    }


    @Override
    public List<AttendanceResponse> getAttendance(Long courseId, Long scheduleId) {

        List<Object[]> rows =
                attendanceRepository.findAttendance(courseId, scheduleId);

        List<AttendanceResponse> response = new ArrayList<>();

        for (Object[] r : rows) {
            AttendanceResponse dto = new AttendanceResponse();
            dto.setPersonnelId((Long) r[0]);
            dto.setFullName((String) r[1]);
            dto.setArmyNo((String) r[2]);
            dto.setRank((String) r[3]);
            dto.setUnitName((String) r[4]);
            dto.setCommand((String) r[5]);
            dto.setDateOfSeniority((LocalDate) r[6]);
            dto.setStatus((String) r[7]); // ATTENDING / NOT_ATTENDING
            response.add(dto);
        }

        return response;
    }


    @Override
    public void updateAttendance(Integer courseId, Long scheduleId,
                                 Long personnelId, String status) {

        PanelAttendance attendance =
                attendanceRepository
                        .findByCourseIdAndScheduleIdAndPersonnelId(
                                courseId, scheduleId, personnelId)
                        .orElse(new PanelAttendance());

        attendance.setCourseId(courseId);
        attendance.setScheduleId(scheduleId);
        attendance.setPersonnelId(personnelId);
        attendance.setAttendanceStatus(status);

        attendanceRepository.save(attendance);
    }


}

