package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.FormationRequestDTO;
import com.example.sena_bhawan.entity.OrbatStructure;
import com.example.sena_bhawan.entity.formation.*;
import com.example.sena_bhawan.repository.OrbatRepository;
import com.example.sena_bhawan.repository.formation.*;
import com.example.sena_bhawan.service.FormationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class FormationServiceImpl implements FormationService {

    private final CommandRepository commandRepository;
    private final CorpsRepository corpsRepository;
    private final DivisionRepository divisionRepository;
    private final BrigadeRepository brigadeRepository;
    private final UnitRepository unitRepository;
    private final OrbatRepository orbatRepository;

    @Override
    public String createFormation(FormationRequestDTO dto) {

        validateHierarchy(dto);

        String type = dto.getFormationType().trim();

        switch (type) {

            case "Command" -> saveCommand(dto);

            case "Corps" -> saveCorps(dto);

            case "Division" -> saveDivision(dto);

            case "Brigade" -> saveBrigade(dto);

            case "Unit" -> saveUnit(dto);

            default -> throw new RuntimeException("Invalid formation type selected");
        }
        return "Created Successfully";
    }

    private void validateHierarchy(FormationRequestDTO dto) {

        String type = dto.getFormationType();

        switch (type) {

            case "Command" -> {
                if (dto.getHqId() == null)
                    throw new RuntimeException("HQ must be selected");
            }

            case "Corps" -> {
                if (dto.getCommandId() == null)
                    throw new RuntimeException("Command must be selected");
            }

            case "Division" -> {
                if (dto.getCorpsId() == null)
                    throw new RuntimeException("Corps must be selected");
            }

            case "Brigade" -> {
                if (dto.getDivisionId() == null)
                    throw new RuntimeException("Division must be selected");
            }

            case "Unit" -> {
                if (dto.getBrigadeId() == null)
                    throw new RuntimeException("Brigade must be selected");
            }
        }
    }

    private void saveOrbatEntry(Long hqId, String commandCode, String corpsCode, String divisionCode, String brigadeCode,
            String unitCode, String type, String name, String location, String susNo, String pin,String areaType, String unitType) {

        OrbatStructure orbat = new OrbatStructure();

        orbat.setHqCode(hqId == null ? null : hqId.toString());
        orbat.setCommandCode(commandCode == null ? null : commandCode.toString());
        orbat.setCorpsCode(corpsCode);
        orbat.setDivisionCode(divisionCode);
        orbat.setBrigadeCode(brigadeCode);
        orbat.setUnitCode(unitCode);

        orbat.setFormationType(type);
        orbat.setName(name);
        orbat.setLocation(location);
        orbat.setSusNo(susNo);
        orbat.setPin(pin);
        orbat.setAreaType(areaType);
        orbat.setUnitType(unitType);

        // formationCode null → entity @PrePersist handle karega
        orbatRepository.save(orbat);
    }

    private void saveCommand(FormationRequestDTO dto) {

        Command command = new Command();
        command.setCommandName(dto.getName());
        command.setLocation(dto.getLocation());
        command.setSusNo(dto.getSusNo());
        command.setPinNo(dto.getPinNo());
        command.setHqId(dto.getHqId());
        command.setCreatedAt(LocalDateTime.now());

        Command saved = commandRepository.save(command);

        String commandCode = saved.getCommandId() == null ? null : saved.getCommandId().toString();

        saveOrbatEntry(dto.getHqId(), commandCode, null, null, null, null,
                "Command", dto.getName(), dto.getLocation(), dto.getSusNo(), dto.getPinNo(),dto.getAreaType(), dto.getUnitType()
        );
    }

    private void saveCorps(FormationRequestDTO dto) {

        Corps corps = new Corps();

        corps.setCorpsName(dto.getName());
        corps.setCommandId(dto.getCommandId());
        corps.setHqId(dto.getHqId());
        corps.setLocation(dto.getLocation());
        corps.setSusNo(dto.getSusNo());
        corps.setPinNo(dto.getPinNo());
        corps.setCreatedAt(LocalDateTime.now());

        Corps saved = corpsRepository.save(corps);

        String corpsCode = generateCode(saved.getCorpsId(), "Corps");
        saved.setCorpsCode(corpsCode);
        String commandCode = String.valueOf(dto.getCommandId());

        saveOrbatEntry(
                dto.getHqId(),
                commandCode,
                corpsCode,
                null,
                null,
                null,
                "Corps",
                dto.getName(),
                dto.getLocation(),
                dto.getSusNo(),
                dto.getPinNo(),
                dto.getAreaType(),
                dto.getUnitType()
        );
    }

    private void saveDivision(FormationRequestDTO dto) {

        Division division = new Division();

        division.setDivisionName(dto.getName());
        division.setCommandId(dto.getCommandId());
        division.setCorpsId(dto.getCorpsId());
        division.setHqId(dto.getHqId());
        division.setLocation(dto.getLocation());
        division.setSusNo(dto.getSusNo());
        division.setPinNo(dto.getPinNo());
        division.setCreatedAt(LocalDateTime.now());
        Division saved = divisionRepository.save(division);

        String divisionCode = generateCode(saved.getDivisionId(), "Division");
        saved.setDivCode(divisionCode);
        String corpsCode = String.format("%02d", dto.getCorpsId());
        String commandCode = String.valueOf( dto.getCommandId());

        saveOrbatEntry(
                dto.getHqId(),
                commandCode,
                corpsCode,
                divisionCode,
                null,
                null,
                "Division",
                dto.getName(),
                dto.getLocation(),
                dto.getSusNo(),
                dto.getPinNo(),
                dto.getAreaType(),
                dto.getUnitType()
        );
    }

    private void saveBrigade(FormationRequestDTO dto) {

        Brigade brigade = new Brigade();

        brigade.setBrigadeName(dto.getName());
        brigade.setCommandId(dto.getCommandId());
        brigade.setCorpsId(dto.getCorpsId());
        brigade.setDivisionId(dto.getDivisionId());
        brigade.setHqId(dto.getHqId());
        brigade.setLocation(dto.getLocation());
        brigade.setSusNo(dto.getSusNo());
        brigade.setPinNo(dto.getPinNo());
        brigade.setCreatedAt(LocalDateTime.now());
        Brigade saved = brigadeRepository.save(brigade);

        String brigadeCode = generateCode(saved.getBrigadeId(), "Brigade");
        saved.setBrigCode(brigadeCode);
        saveOrbatEntry(
                dto.getHqId(),
                String.valueOf(dto.getCommandId()),
                String.format("%02d", dto.getCorpsId()),
                String.format("%02d", dto.getDivisionId()),
                brigadeCode,
                null,
                "Brigade",
                dto.getName(),
                dto.getLocation(),
                dto.getSusNo(),
                dto.getPinNo(),
                dto.getAreaType(),
                dto.getUnitType()
        );
    }

    private void saveUnit(FormationRequestDTO dto) {

        Unit unit = new Unit();

        unit.setUnitName(dto.getName());
        unit.setCommandId(dto.getCommandId());
        unit.setCorpsId(dto.getCorpsId());
        unit.setDivisionId(dto.getDivisionId());
        unit.setBrigadeId(dto.getBrigadeId());
        unit.setHqId(dto.getHqId());
        unit.setLocation(dto.getLocation());
        unit.setSusNo(dto.getSusNo());
        unit.setPinNo(dto.getPinNo());
        unit.setCreatedAt(LocalDateTime.now());

        Unit saved = unitRepository.save(unit);

        String unitCode = generateCode(saved.getUnitId(), "Unit");
        saved.setUnitCode(unitCode);

        saveOrbatEntry(
                dto.getHqId(),
                String.valueOf( dto.getCommandId()),
                String.format("%02d", dto.getCorpsId()),
                String.format("%02d", dto.getDivisionId()),
                String.format("%02d", dto.getBrigadeId()),
                unitCode,
                "Unit",
                dto.getName(),
                dto.getLocation(),
                dto.getSusNo(),
                dto.getPinNo(),
                dto.getAreaType(),
                dto.getUnitType()
        );

//        Unit saved = unitRepository.save(unit);
//        String code = generateCode(saved.getUnitId(), "Unit");
//        saved.setUnitCode(code);
//        unitRepository.save(saved);
    }

    private String generateCode(Long id, String formationType) {

        if (id == null)
            throw new RuntimeException("ID cannot be null for code generation");

        switch (formationType) {

            case "Corps":
            case "Division":
            case "Brigade":
                return String.format("%02d", id); // 2 digit

            case "Unit":
                return String.format("%03d", id); // 3 digit

            default:
                throw new RuntimeException("Invalid formation type for code generation");
        }
    }
}
