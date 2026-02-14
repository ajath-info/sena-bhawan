package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.OfficerPostingRequestDTO;
import com.example.sena_bhawan.entity.OfficerPosting;
import jakarta.transaction.Transactional;

public interface OfficerPostingService {

    OfficerPosting createPosting(OfficerPostingRequestDTO dto);

    @Transactional
    OfficerPosting createPosting(OfficerPosting posting);
}
