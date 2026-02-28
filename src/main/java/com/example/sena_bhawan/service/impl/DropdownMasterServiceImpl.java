package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.DropdownDTO;
import com.example.sena_bhawan.repository.DropdownMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DropdownMasterServiceImpl implements MasterDropdownService{

    @Autowired
    DropdownMasterRepository dropdownMasterRepository;

    public List<DropdownDTO> getByType(String type) {
        return dropdownMasterRepository.findByTypeAndStatus(type, 1)
                .stream()
                .map(d -> new DropdownDTO(d.getId(), d.getName()))
                .collect(Collectors.toList());
    }
}
