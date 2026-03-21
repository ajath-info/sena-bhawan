package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.RemarksHistoryDTO;
import com.example.sena_bhawan.dto.RemarksUpdateDTO;
import com.example.sena_bhawan.entity.RemarksUpdate;
import com.example.sena_bhawan.entity.Personnel; // Assuming you have a Personnel entity
import com.example.sena_bhawan.repository.RemarksUpdateRepository;
import com.example.sena_bhawan.repository.PersonnelRepository; // Add this repository
import com.example.sena_bhawan.service.RemarksUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RemarksUpdateServiceImpl implements RemarksUpdateService {

    @Autowired
    private RemarksUpdateRepository remarksUpdateRepository;

    @Autowired
    private PersonnelRepository personnelRepository; // Inject PersonnelRepository

    @Override
    public RemarksUpdate saveOrUpdate(RemarksUpdateDTO dto) {
        // Validate personnelId is not null
        if (dto.getPersonnelId() == null) {
            throw new IllegalArgumentException("Personnel ID cannot be null");
        }

        // Validate if personnel exists in database
        Personnel personnel = personnelRepository.findById(dto.getPersonnelId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Personnel with ID " + dto.getPersonnelId() + " does not exist"
                ));

        // Always create a new entry for history
        RemarksUpdate remarks = new RemarksUpdate();
        remarks.setPersonnelId(dto.getPersonnelId());

        // Set remark type: if status is provided from radio button, use that, otherwise default to "General"
        if (dto.getRemarkType() != null && !dto.getRemarkType().isEmpty()) {
            remarks.setRemarkType(dto.getRemarkType());
        } else {
            remarks.setRemarkType("General");
        }

        remarks.setGeneralRemarks(dto.getGeneralRemarks());
        remarks.setCourseId(dto.getCourseId());

        // Set course name for display in history
        if (dto.getCourseName() != null) {
            remarks.setCourseName(dto.getCourseName());
        }

        return remarksUpdateRepository.save(remarks);
    }

    @Override
    public List<RemarksHistoryDTO> getRemarksHistory(Long personnelId) {
        if (personnelId == null) {
            throw new IllegalArgumentException("Personnel ID cannot be null");
        }

        // Check if personnel exists
        if (!personnelRepository.existsById(personnelId)) {
            throw new IllegalArgumentException(
                    "Personnel with ID " + personnelId + " does not exist"
            );
        }

        // Get entities from database
        List<RemarksUpdate> remarksList = remarksUpdateRepository.findByPersonnelIdOrderByIdDesc(personnelId);

        // Manually convert each entity to DTO
        return remarksList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Helper method to convert Entity to DTO
    private RemarksHistoryDTO convertToDTO(RemarksUpdate entity) {
        return new RemarksHistoryDTO(
                entity.getId(),
                entity.getPersonnelId(),
                entity.getRemarkType() != null ? entity.getRemarkType() : "General",
                entity.getGeneralRemarks(),
                entity.getCourseName() != null ? entity.getCourseName() : "-",
                entity.getCreatedAt()
        );
    }

}