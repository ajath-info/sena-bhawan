package com.example.sena_bhawan.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NominationDTO {

    private Long personnelId;
    private String status; // ATTENDING / NOT_ATTENDING
}
