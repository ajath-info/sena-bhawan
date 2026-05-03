package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.entity.Personnel;
import com.example.sena_bhawan.entity.PersonnelInformation;
import com.example.sena_bhawan.repository.PersonnelInformationRepository;
import com.example.sena_bhawan.repository.PersonnelRepository;
import com.example.sena_bhawan.service.PersonnelInformationService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PersonnelInformationServiceImpl implements PersonnelInformationService {

    private final PersonnelInformationRepository repository;

    public PersonnelInformationServiceImpl(
            PersonnelInformationRepository repository) {
        this.repository = repository;
    }

    @Override
    public PersonnelInformation save(PersonnelInformation info) {
        return repository.save(info);
    }

    @Override
    public PersonnelInformation update(Long id, PersonnelInformation updatedInfo) {
        PersonnelInformation existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personnel not found with id: " + id));

        // update fields
        existing.setPersonnelId(updatedInfo.getPersonnelId());
        existing.setRank(updatedInfo.getRank());
        existing.setFullName(updatedInfo.getFullName());
        existing.setCaseType(updatedInfo.getCaseType());
        existing.setCaseId(updatedInfo.getCaseId());
        existing.setDateOfFiling(updatedInfo.getDateOfFiling());
        existing.setCurrentStatus(updatedInfo.getCurrentStatus());
        existing.setFinalOutcome(updatedInfo.getFinalOutcome());

        return repository.save(existing);
    }

    @Override
    public List<PersonnelInformation> getAll() {
        return List.of();
    }

    @Override
    public List<PersonnelInformation> getByPersonnelId(Long personnelId) {
        return repository.findByPersonnelId(personnelId);
    }
}


