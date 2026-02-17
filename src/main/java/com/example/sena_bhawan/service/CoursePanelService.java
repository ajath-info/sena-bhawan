package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.CoursePanelRequest;
import com.example.sena_bhawan.dto.CoursePanelResponse;
import com.example.sena_bhawan.dto.OfficerStatusResponse;
import com.example.sena_bhawan.dto.OngoingCoursesResponse;
import com.example.sena_bhawan.entity.CoursePanelNomination;
import com.example.sena_bhawan.projection.AttendanceStatusProjection;
import com.example.sena_bhawan.projection.OngoingCoursesProjection;
import com.example.sena_bhawan.repository.CoursePanelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoursePanelService {

    private final CoursePanelRepository repository;

    /* ===============================
       STEP-3 : SAVE PANEL
    ================================ */
    @Transactional
    public void savePanel(CoursePanelRequest request) {


//        repository.deleteByScheduleId(request.getScheduleId());

        List<CoursePanelNomination> records =
                request.getNominations().stream()
                        .map(n -> CoursePanelNomination.builder()
                                .scheduleId(request.getScheduleId())
                                .personnelId(n.getPersonnelId())
                                .attendanceStatus(n.getStatus())
                                .build())
                        .toList();

        repository.saveAll(records);
    }

    /* ===============================
       STEP-4 : FETCH PANEL
    ================================ */
    public List<CoursePanelResponse> getPanel(Long scheduleId) {

        return repository.findByScheduleId(scheduleId)
                .stream()
                .map(r -> new CoursePanelResponse(
                        r.getPersonnelId(),
                        r.getAttendanceStatus()
                ))
                .toList();
    }

    public OngoingCoursesResponse getOngoingCoursesStrength() {
        // Fetch data using single query
        List<OngoingCoursesProjection> projections =
                repository.getOngoingCoursesWithCountsAlternative();

        // Prepare response lists
        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();

        for (OngoingCoursesProjection projection : projections) {
            labels.add(projection.getCourseName());
            data.add(projection.getOfficerCount().intValue());
        }

        return OngoingCoursesResponse.builder()
                .labels(labels)
                .data(data)
                .chartType("bar")
                .title("Ongoing Courses – Officer Strength")
                .build();
    }

    public OfficerStatusResponse getOfficerStatusOverview() {
        List<AttendanceStatusProjection> projections =
                repository.getAttendanceStatusCounts();

        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();

        for (AttendanceStatusProjection projection : projections) {
            labels.add(projection.getAttendanceStatus());  // Raw status from DB
            data.add(projection.getCount().intValue());     // Count
        }

        return OfficerStatusResponse.builder()
                .labels(labels)
                .data(data)
                .build();
    }
}
