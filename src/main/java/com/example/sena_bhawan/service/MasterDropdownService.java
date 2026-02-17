package com.example.sena_bhawan.service;

import java.util.List;

public interface MasterDropdownService {

    List<String> getMedicalCategoryDropdown();
    List<String> getEstablishmentTypeDropdown();
    List<String> getAreaTypeDropdown();
    List<String> getCivilQualificationDropdown();
    List<String> getSportsDropdown();
    List<Integer> getPostingDueMonthsDropdown();
}

