package com.example.sena_bhawan.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CoursePanelRequest {

    private Long scheduleId;
    private List<NominationDTO> nominations;
}