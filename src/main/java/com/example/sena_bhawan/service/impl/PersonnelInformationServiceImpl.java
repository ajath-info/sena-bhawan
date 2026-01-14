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
    public List<PersonnelInformation> getAll() {
        return List.of();
    }

    @Override
    public List<PersonnelInformation> getByPersonnelId(Long personnelId) {
        return repository.findByPersonnelId(personnelId);
    }
}


