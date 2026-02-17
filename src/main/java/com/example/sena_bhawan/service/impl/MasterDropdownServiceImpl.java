package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.repository.*;
import com.example.sena_bhawan.service.MasterDropdownService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MasterDropdownServiceImpl implements MasterDropdownService {

    private final MedicalCategoryMasterRepository medicalRepo;
    private final EstablishmentTypeMasterRepository estRepo;
    private final AreaTypeMasterRepository areaRepo;
    private final CivilQualificationMasterRepository civilRepo;
    private final SportsMasterRepository sportsRepo;
    private final PostingDueMonthsMasterRepository postingRepo;

    @Override
    public List<String> getMedicalCategoryDropdown() {
        return medicalRepo.findActiveMedicalCategories();
    }

    @Override
    public List<String> getEstablishmentTypeDropdown() {
        return estRepo.findActiveEstablishments();
    }

    @Override
    public List<String> getAreaTypeDropdown() {
        return areaRepo.findActiveAreaTypes();
    }

    @Override
    public List<String> getCivilQualificationDropdown() {
        return civilRepo.findActiveQualifications();
    }

    @Override
    public List<String> getSportsDropdown() {
        return sportsRepo.findActiveSports();
    }

    @Override
    public List<Integer> getPostingDueMonthsDropdown() {
        return postingRepo.findActiveMonths();
    }
}
