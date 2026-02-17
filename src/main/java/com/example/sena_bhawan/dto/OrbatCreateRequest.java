package com.example.sena_bhawan.dto;

import lombok.Data;

@Data
public class OrbatCreateRequest {

    private Long hqId;

    private String formationType;

    private Long commandId;
    private Long corpsId;
    private Long divisionId;
    private Long brigadeId;

    private String name;
    private String location;
    private String sosNo;
    private String pin;
    private String unitName;

    private String formationCode;
    private String areaType;
    private String unitType;

}
