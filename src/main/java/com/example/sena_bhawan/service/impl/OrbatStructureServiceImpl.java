package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.OrbatSimpleDTO;
import com.example.sena_bhawan.repository.OrbatStructureRepository;
import com.example.sena_bhawan.service.OrbatStructureService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrbatStructureServiceImpl implements OrbatStructureService {

    private final OrbatStructureRepository repository;

    public OrbatStructureServiceImpl(OrbatStructureRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<OrbatSimpleDTO> getAllCommands() {
        return repository.findAllCommands();
    }

    @Override
    public List<OrbatSimpleDTO> getAllCorps() {
        return repository.findAllCorps();
    }


    @Override
    public List<OrbatSimpleDTO> getCorpsByCommand(String commandName) {
        return repository.findCorpsByCommandName(commandName);
    }
}
