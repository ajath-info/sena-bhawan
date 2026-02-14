package com.example.sena_bhawan.repository;

import com.example.sena_bhawan.entity.CoursePanelNomination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoursePanelRepository
        extends JpaRepository<CoursePanelNomination, Long> {

    void deleteByScheduleId(Long scheduleId);

    List<CoursePanelNomination> findByScheduleId(Long scheduleId);
}
