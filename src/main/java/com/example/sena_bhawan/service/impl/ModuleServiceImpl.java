package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.entity.ModuleEntity;
import com.example.sena_bhawan.repository.ModuleRepository;
import com.example.sena_bhawan.service.ModuleService;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository repo;

    public ModuleServiceImpl(ModuleRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<ModuleEntity> getAllModules() {
        return repo.findAll();
    }

    @Override
    public ModuleEntity getModuleById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Module not found with id: " + id));
    }

    @Override
    public ModuleEntity addModule(ModuleEntity module) {
        return repo.save(module);
    }

    @Override
    public ModuleEntity updateModule(Long id, ModuleEntity module) {
        ModuleEntity existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Module not found with id: " + id));

        existing.setName(module.getName());

        return repo.save(existing);
    }

    @Override
    public void deleteModule(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Module not found with id: " + id);
        }
        repo.deleteById(id);
    }
}
