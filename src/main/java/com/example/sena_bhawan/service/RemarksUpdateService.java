package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.RemarksHistoryDTO;
import com.example.sena_bhawan.dto.RemarksUpdateDTO;
import com.example.sena_bhawan.entity.RemarksUpdate;

import java.util.List;

public interface RemarksUpdateService {
    RemarksUpdate saveOrUpdate(RemarksUpdateDTO dto);
    List<RemarksHistoryDTO> getRemarksHistory(Long personnelId);
}
