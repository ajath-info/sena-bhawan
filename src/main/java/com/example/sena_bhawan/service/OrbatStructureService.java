package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.DropdownDTO;
import com.example.sena_bhawan.dto.OrbatDropdownDTO;
import com.example.sena_bhawan.dto.OrbatSearchDTO;
import com.example.sena_bhawan.dto.OrbatSimpleDTO;

import java.util.List;

public interface OrbatStructureService {

    List<OrbatSearchDTO> searchUnits(String term);
    void validateSearchTerm(String term);

    List<OrbatDropdownDTO> getByFormationType(String formationType);

    List<DropdownDTO> getAllHq();

    List<DropdownDTO> getAllCommands();

    List<DropdownDTO> getAllCorps();

    List<DropdownDTO> getAllDivisions();

    List<DropdownDTO> getAllBrigades();

    List<DropdownDTO> getAllUnits();

    List<OrbatSimpleDTO> getCorpsByCommand(String commandName);

    List<String> getCommandDropdown();

    List<String> getCorpsDropdown();

    List<String> getDivisionDropdown();

    // Add these new methods for filtered dropdowns
    List<DropdownDTO> getCorpsByCommandId(Long commandId);
    List<DropdownDTO> getDivisionsByCorpsId(Long corpsId);
    List<DropdownDTO> getBrigadesByDivisionId(Long divisionId);

}
