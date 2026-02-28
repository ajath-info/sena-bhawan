package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.DropdownDTO;
import com.example.sena_bhawan.dto.OrbatDropdownDTO;
import com.example.sena_bhawan.dto.OrbatSimpleDTO;
import com.example.sena_bhawan.entity.OrbatStructure;
import com.example.sena_bhawan.repository.OrbatRepository;
import com.example.sena_bhawan.repository.OrbatStructureRepository;

import com.example.sena_bhawan.repository.formation.*;
import com.example.sena_bhawan.service.OrbatStructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrbatStructureServiceImpl implements OrbatStructureService {

    private final OrbatStructureRepository repository;
    private final OrbatRepository orbatRepository;
    private final ArmyHQRepository armyHQRepository;
    private final CommandRepository commandRepository;
    private final CorpsRepository corpsRepository;
    private final DivisionRepository divisionRepository;
    private final BrigadeRepository brigadeRepository;
    private final UnitRepository unitRepository;


    @Override
    public List<DropdownDTO> getAllHq() {
        return armyHQRepository.findAll().stream().map(e-> {
            DropdownDTO d = new DropdownDTO();
            d.setId(e.getHqId());
            d.setName(e.getHqName());
            return d;
        }).toList();
    }

    @Override
    public List<DropdownDTO> getAllCommands() {
        return commandRepository.findAll().stream().map(e-> {
            DropdownDTO d = new DropdownDTO();
            d.setId(e.getCommandId());
            d.setName(e.getCommandName());
            return d;
        }).toList();
    }

    @Override
    public List<DropdownDTO> getAllCorps() {
        return corpsRepository.findAll().stream().map(e-> {
            DropdownDTO d = new DropdownDTO();
            d.setId(e.getCorpsId());
            d.setName(e.getCorpsName());
            return d;
        }).toList();
    }

    @Override
    public List<DropdownDTO> getAllDivisions() {
        return divisionRepository.findAll().stream().map(e-> {
            DropdownDTO d = new DropdownDTO();
            d.setId(e.getDivisionId());
            d.setName(e.getDivisionName());
            return d;
        }).toList();
    }


    @Override
    public List<DropdownDTO> getAllBrigades() {
        return brigadeRepository.findAll().stream().map(e-> {
            DropdownDTO d = new DropdownDTO();
            d.setId(e.getBrigadeId());
            d.setName(e.getBrigadeName());
            return d;
        }).toList();
    }

    @Override
    public List<DropdownDTO> getAllUnits() {
        return unitRepository.findAll().stream().map(e-> {
            DropdownDTO d = new DropdownDTO();
            d.setId(e.getUnitId());
            d.setName(e.getUnitName());
            return d;
        }).toList();
    }

    public List<OrbatDropdownDTO> getByFormationType(String formationType) {
        List<OrbatStructure> orbatList = orbatRepository.findByFormationType(formationType);

        return orbatList.stream()
                .map(orbat -> new OrbatDropdownDTO(
                        orbat.getId(),
                        orbat.getFormationType(),
                        orbat.getName()
                ))
                .collect(Collectors.toList());
    }


    @Override
    public List<OrbatSimpleDTO> getCorpsByCommand(String commandName) {
        return repository.findCorpsByCommandName(commandName);
    }

    @Override
    public List<String> getCommandDropdown() {
        return repository.findDistinctCommandNames();
    }

    @Override
    public List<String> getCorpsDropdown() {
        return repository.findDistinctCorpsNames();
    }

    @Override
    public List<String> getDivisionDropdown() {
        return repository.findDistinctDivisionNames();
    }

}
