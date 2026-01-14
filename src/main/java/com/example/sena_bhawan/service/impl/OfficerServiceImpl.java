package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.entity.Officer;
import com.example.sena_bhawan.repository.OfficerRepository;
import com.example.sena_bhawan.service.OfficerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfficerServiceImpl implements OfficerService {

    private final OfficerRepository repository;

    public OfficerServiceImpl(OfficerRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Officer> getAllOfficers() {
        return repository.findAll();
    }

    @Override
    public Officer getOfficerById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Officer not found"));
    }

    @Override
    public long getOfficerCount() {
        return repository.count();
    }

    @Override
    public List<Officer> getOfficersByStatus(String status) {
        return repository.findByStatus(status);
    }

    @Override
    public List<Officer> getOfficersByRank(String rank) {
        return repository.findByRank(rank);
    }

    @Override
    public List<Officer> getOfficersByUnit(String unit) {
        return repository.findByUnitName(unit);
    }
}
