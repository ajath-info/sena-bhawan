package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.DropdownDTO;
import com.example.sena_bhawan.dto.OrbatDropdownDTO;
import com.example.sena_bhawan.dto.OrbatSimpleDTO;

import java.util.List;

public interface OrbatStructureService {

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

}
