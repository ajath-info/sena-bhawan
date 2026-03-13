package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.repository.*;
import com.example.sena_bhawan.service.MasterDropdownService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MasterDropdownServiceImpl implements MasterDropdownService {
    private final EstablishmentTypeMasterRepository estRepo;
    private final PostingDueMonthsMasterRepository postingRepo;
    private final PersonnelRepository personnelRepository;
    private final OrbatStructureRepository orbatStructureRepository;
    private final PersonnelAdditionalQualificationsRepository personnelAdditionalQualificationsRepository;
    private final PersonnelSportsRepository personnelSportsRepository;

    @Override
    public List<String> getMedicalCategoryDropdown() {
        return personnelRepository.getMedicalCategory();
    }

    @Override
    public List<String> getEstablishmentTypeDropdown() {
        return estRepo.findActiveEstablishments();
    }

    @Override
    public List<String> getAreaTypeDropdown() {
        return orbatStructureRepository.getAllAreaType();
    }

    @Override
    public List<String> getUnitDropdown() {
        return orbatStructureRepository.findAllUnitNames();
    }

    @Override
    public List<String> getCivilQualificationDropdown() {
        return personnelAdditionalQualificationsRepository.additionalQualificationList();
    }

    @Override
    public List<String> getSportsDropdown() {
        return personnelSportsRepository.additionalQualificationList();
    }

    @Override
    public List<Integer> getPostingDueMonthsDropdown() {
        return postingRepo.findActiveMonths();
    }
}
