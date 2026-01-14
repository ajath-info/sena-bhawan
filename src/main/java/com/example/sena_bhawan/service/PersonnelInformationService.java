package com.example.sena_bhawan.service;


import com.example.sena_bhawan.entity.PersonnelInformation;

import java.util.List;

public interface PersonnelInformationService {

    PersonnelInformation save(PersonnelInformation info);

    List<PersonnelInformation> getAll();



    List<PersonnelInformation> getByPersonnelId(Long personnelId);


}



