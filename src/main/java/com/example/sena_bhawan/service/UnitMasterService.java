package com.example.sena_bhawan.service;

import com.example.sena_bhawan.entity.UnitMaster;
import com.example.sena_bhawan.repository.UnitMasterRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UnitMasterService {

    private final UnitMasterRepository repo;

    public UnitMasterService(UnitMasterRepository repo) {
        this.repo = repo;
    }

    public UnitMaster create(UnitMaster unit) {
        return repo.save(unit);
    }

    public List<UnitMaster> getAll() {
        return repo.findAll();
    }

    public UnitMaster getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public UnitMaster update(Long id, UnitMaster updatedUnit) {
        return repo.findById(id)
                .map(existing -> {
                    existing.setUnitName(updatedUnit.getUnitName());
                    existing.setLocation(updatedUnit.getLocation());
                    return repo.save(existing);
                })
                .orElse(null);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}

