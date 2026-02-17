package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.RemarksUpdateDTO;
import com.example.sena_bhawan.entity.RemarksUpdate;

public interface RemarksUpdateService {
    public RemarksUpdate saveOrUpdate(RemarksUpdateDTO dto);
}
