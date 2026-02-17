package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.*;
import com.example.sena_bhawan.entity.OrbatStructure;

import java.util.List;

public interface OrbatService {

    OrbatStructure createFormation(OrbatCreateRequest request);

    Object getDropdown(String type, Long parentId);

    OrbatTreeResponse getOrbatTree();

    List<String> getDistinctSosNumbers();

    List<OrbatStructure> getSosList();



    // ================= ORBAT NAME DROPDOWNS =================
    List<String> getCommandDropdown();

    List<String> getCorpsDropdown();

    List<String> getDivisionDropdown();

    // ================= FILTER DROPDOWNS =================
    List<String> getRankDropdown();

    List<String> getMedicalCategoryDropdown();

    List<String> getEstablishmentTypeDropdown();

    List<String> getAreaTypeDropdown();

    List<String> getCivilQualificationDropdown();

    List<String> getSportsDropdown();

    List<Integer> getPostingDueMonthsDropdown();
}
