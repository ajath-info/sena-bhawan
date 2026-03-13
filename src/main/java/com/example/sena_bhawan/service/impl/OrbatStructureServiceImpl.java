package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.DropdownDTO;
import com.example.sena_bhawan.dto.OrbatDropdownDTO;
import com.example.sena_bhawan.dto.OrbatSearchDTO;
import com.example.sena_bhawan.dto.OrbatSimpleDTO;
import com.example.sena_bhawan.entity.OrbatStructure;
import com.example.sena_bhawan.repository.OrbatRepository;
import com.example.sena_bhawan.repository.OrbatStructureRepository;

import com.example.sena_bhawan.repository.formation.*;
import com.example.sena_bhawan.service.OrbatStructureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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
    public List<OrbatSearchDTO> searchUnits(String term) {
        // Step 1: Validate search term
        validateSearchTerm(term);

        log.info("Searching units with term: {}", term);

        // Step 2: Search in database
        Pageable pageable = PageRequest.of(0, 10);
        List<OrbatStructure> units = orbatRepository.findDistinctByNameStartingWith(term, pageable);

        // Step 3: Convert to DTO (only id and name)
        return units.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void validateSearchTerm(String term) {
        if (term == null || term.trim().length() < 3) {
            throw new RuntimeException("Minimum 3 characters required for search");
        }
    }

    private OrbatSearchDTO convertToDTO(OrbatStructure unit) {
        OrbatSearchDTO dto = new OrbatSearchDTO();
        dto.setId(unit.getId());
        dto.setName(unit.getName());
        return dto;
    }

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
        return repository.findAllCommandNames();
    }

    @Override
    public List<String> getCorpsDropdown() {
        return repository.findAllCorpsNames();
    }

    @Override
    public List<String> getDivisionDropdown() {
        return repository.findDistinctDivisionNames();
    }

}
