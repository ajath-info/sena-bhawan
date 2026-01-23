package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.entity.DecorationMaster;
import com.example.sena_bhawan.repository.DecorationMasterRepository;
import com.example.sena_bhawan.service.DecorationMasterService;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DecorationMasterServiceImpl implements DecorationMasterService {

    private final DecorationMasterRepository repository;

    public DecorationMasterServiceImpl(DecorationMasterRepository repository) {
        this.repository = repository;
    }

    @Override
    public DecorationMaster addDecoration(DecorationMaster decoration) {
        return repository.save(decoration);
    }

    @Override
    public DecorationMaster updateDecoration(Long id, DecorationMaster decoration) {
        DecorationMaster existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Decoration not found"));

        existing.setAwardCategory(decoration.getAwardCategory());
        existing.setAwardName(decoration.getAwardName());
        existing.setDecoration(decoration.getDecoration());

        return repository.save(existing);
    }

    @Override
    public void deleteDecoration(Long id) {
        repository.deleteById(id);
    }

    @Override
    public DecorationMaster getDecorationById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Decoration not found"));
    }

    @Override
    public List<DecorationMaster> getAllDecorations() {
        return repository.findAll();
    }

    @Override
    public List<Object> getDecorationDropdown() {
        return repository.findAll().stream()
                .map(d -> new Object() {
                    public final Long id = d.getId();
                    public final String name = d.getAwardName();
                    public final String suffix = d.getDecoration();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllCategories() {
        return repository.findAll()
                .stream()
                .map(DecorationMaster::getAwardCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Object> getAwardsByCategory(String category) {
        return repository.findAll()
                .stream()
                .filter(d -> d.getAwardCategory().equalsIgnoreCase(category))
                .map(d -> new Object() {
                    public final Long id = d.getId();
                    public final String name = d.getAwardName();
                    public final String suffix = d.getDecoration();
                })
                .collect(Collectors.toList());
    }

}
