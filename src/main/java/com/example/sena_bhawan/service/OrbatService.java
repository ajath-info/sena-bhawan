package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.*;
import com.example.sena_bhawan.entity.OrbatStructure;

import java.util.List;

public interface OrbatService {

//    OrbatStructure createFormation(OrbatCreateRequest request);

    Object getDropdown(String type, Long parentId);

    OrbatTreeResponse getOrbatTree();

    List<String> getDistinctSusNumbers();

    List<OrbatStructure> getSusList();

}
