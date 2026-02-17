package com.example.sena_bhawan.service.impl;


import com.example.sena_bhawan.dto.DashboardStatsResponse;
import com.example.sena_bhawan.repository.CourseScheduleRepository;
import com.example.sena_bhawan.repository.PersonnelRepository;
import com.example.sena_bhawan.repository.PostingDetailsRepository;
import com.example.sena_bhawan.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final PersonnelRepository personnelRepository;
    private final PostingDetailsRepository postingDetailsRepository;
    private final CourseScheduleRepository courseScheduleRepository;

    @Override
    public DashboardStatsResponse getDashboardStats() {
        LocalDate today = LocalDate.now();
        
        // 1. Total Personnel
        long totalPersonnel = personnelRepository.getTotalPersonnelCount();
        
        // 2. Active Postings
        long activePostings = postingDetailsRepository.countActivePostingsWithNullToDate(today);
        
        // 3. Courses Ongoing
        long coursesOngoing = courseScheduleRepository.countOngoingCourses(today);

        // 4. Pending Transfers - Count all records with future from_date
        long pendingTransfers = postingDetailsRepository.countPendingTransfers(today);
        
        return DashboardStatsResponse.builder()
                .totalPersonnel(totalPersonnel)
                .activePostings(activePostings)
                .coursesOngoing(coursesOngoing)
                .pendingTransfers(pendingTransfers)
                .build();
    }
}