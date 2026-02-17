package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.OrbatSimpleDTO;

import java.util.List;

public interface OrbatStructureService {

    List<OrbatSimpleDTO> getAllCommands();

    List<OrbatSimpleDTO> getAllCorps();

    List<OrbatSimpleDTO> getCorpsByCommand(String commandName);

    List<String> getCommandDropdown();

    List<String> getCorpsDropdown();

    List<String> getDivisionDropdown();

}
