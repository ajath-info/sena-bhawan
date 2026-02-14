package com.example.sena_bhawan.service;

import com.example.sena_bhawan.entity.ModuleEntity;
import java.util.List;

public interface ModuleService {

    List<ModuleEntity> getAllModules();
    ModuleEntity getModuleById(Long id);
    ModuleEntity addModule(ModuleEntity module);
    ModuleEntity updateModule(Long id, ModuleEntity module);
    void deleteModule(Long id);
}
