package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.DropdownDTO;

import java.util.List;

public interface MasterDropdownService {
    public List<DropdownDTO> getByType(String type);
}
