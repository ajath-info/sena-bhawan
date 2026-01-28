package com.example.sena_bhawan.service;

import com.example.sena_bhawan.dto.CreatePersonnelRequest;
import com.example.sena_bhawan.entity.Personnel;
import java.util.List;

public interface PersonnelService {

    List<Personnel> getallPersonnels() ;

    Personnel getPersonnelById(Long id);

    Long createPersonnel(CreatePersonnelRequest request);
}
