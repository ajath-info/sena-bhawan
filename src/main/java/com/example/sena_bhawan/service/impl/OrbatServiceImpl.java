package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.*;
import com.example.sena_bhawan.entity.OrbatStructure;
import com.example.sena_bhawan.repository.OrbatRepository;
import com.example.sena_bhawan.service.OrbatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrbatServiceImpl implements OrbatService {

    private final OrbatRepository repo;

//    @Override
//    public OrbatStructure createFormation(OrbatCreateRequest req) {
//
//        OrbatStructure row = new OrbatStructure();
//
//        row.setFormationType(req.getFormationType());
//        row.setName(req.getName());
//        row.setHqId(req.getHqId());
//
//        row.setLocation(req.getLocation());
//        row.setSosNo(req.getSosNo());
//        row.setPin(req.getPin());
//        row.setUnitName(req.getUnitName()); // client requirement
//        row.setUnitName(null); // client requirement
//
//        // Fill parent names (same as your existing logic)
//        if (req.getCommandId() != null) {
//            var cmd = repo.findById(req.getCommandId()).orElse(null);
//            if (cmd != null) row.setCommandName(cmd.getName());
//        }
//
//        if (req.getCorpsId() != null) {
//            var corps = repo.findById(req.getCorpsId()).orElse(null);
//            if (corps != null) {
//                row.setCommandName(corps.getCommandName());
//                row.setCorpsName(corps.getName());
//            }
//        }
//
//        if (req.getDivisionId() != null) {
//            var div = repo.findById(req.getDivisionId()).orElse(null);
//            if (div != null) {
//                row.setCommandName(div.getCommandName());
//                row.setCorpsName(div.getCorpsName());
//                row.setDivisionName(div.getName());
//            }
//        }
//
//        if (req.getBrigadeId() != null) {
//            var brig = repo.findById(req.getBrigadeId()).orElse(null);
//            if (brig != null) {
//                row.setCommandName(brig.getCommandName());
//                row.setCorpsName(brig.getCorpsName());
//                row.setDivisionName(brig.getDivisionName());
//                row.setBrigadeName(brig.getName());
//            }
//        }
//
//        // Set parent_id
//        row.setParentId(
//                switch (req.getFormationType()) {
//                    case "command" -> req.getHqId();
//                    case "corps" -> req.getCommandId();
//                    case "division" -> req.getCorpsId();
//                    case "brigade" -> req.getDivisionId();
//                    case "battalion" -> req.getBrigadeId();
//                    default -> null;
//                }
//        );
//
//        // 1️⃣ SAVE FIRST (to get ID)
//        OrbatStructure saved = repo.save(row);
//
//        // 2️⃣ GENERATE UNIQUE FORMATION CODE USING ID
//        String formationCode = buildFormationCode(req, saved.getId());
//
//        saved.setFormationCode(formationCode);
//
//        // 3️⃣ SAVE AGAIN
//        return repo.save(null);
//    }

    private String buildFormationCode(OrbatCreateRequest req, Long unitId) {

        return String.format("%01d%01d%02d%02d%02d%03d",
                req.getHqId() != null ? req.getHqId() : 0,
                req.getCommandId() != null ? req.getCommandId() : 0,
                req.getCorpsId() != null ? req.getCorpsId() : 0,
                req.getDivisionId() != null ? req.getDivisionId() : 0,
                req.getBrigadeId() != null ? req.getBrigadeId() : 0,
                unitId != null ? unitId : 0
        );
    }



    @Override
    public Object getDropdown(String type, Long parentId) {
        if (parentId == null)
            return repo.findByFormationType(type);

        return repo.findByParentId(parentId);
    }

    @Override
    public OrbatTreeResponse getOrbatTree() {

        OrbatStructure root = repo.findById(1L).orElseThrow();
        return buildTree(root);
    }

    @Override
    public List<String> getDistinctSusNumbers() {
        return repo.findDistinctSusNumbers();
    }

    @Override
    public List<OrbatStructure> getSusList() {
        return repo.findAllWithSusNumbers();
    }

    private OrbatTreeResponse buildTree(OrbatStructure root) {

        OrbatTreeResponse node = new OrbatTreeResponse();

        node.setId(root.getId());
        node.setName(root.getName());
        node.setFormationType(root.getFormationType());
        node.setFormationCode(root.getFormationCode());

        List<OrbatTreeResponse> children =
                repo.findByParentId(root.getId()).stream()
                        .map(this::buildTree)
                        .collect(Collectors.toList());

        node.setChildren(children);

        return node;
    }

}
