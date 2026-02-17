package com.example.sena_bhawan.service.impl;

import com.example.sena_bhawan.dto.RemarksUpdateDTO;
import com.example.sena_bhawan.entity.RemarksUpdate;
import com.example.sena_bhawan.repository.RemarksUpdateRepository;
import com.example.sena_bhawan.service.RemarksUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RemarksUpdateServiceImpl implements RemarksUpdateService {

    @Autowired
    RemarksUpdateRepository remarksUpdateRepository;

    public RemarksUpdate saveOrUpdate(RemarksUpdateDTO dto) {

        if(dto.getPersonnelId() == null){
            return null;
        }
        // Check if record already exists for personnel
        RemarksUpdate remarks = remarksUpdateRepository
                .findByPersonnelId(dto.getPersonnelId())
                .orElse(new RemarksUpdate());

        remarks.setPersonnelId(dto.getPersonnelId());
        remarks.setBeforeDetailment(dto.getBeforeDetailment());
        remarks.setAfterDetailment(dto.getAfterDetailment());
        remarks.setGeneralRemarks(dto.getGeneralRemarks());

        return remarksUpdateRepository.save(remarks);
    }
}
