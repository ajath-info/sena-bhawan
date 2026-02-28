package com.example.sena_bhawan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstablishmentRequest {

    private String establishmentType;

    private Integer totalAuthorizedOfficers;
    private Integer totalHardScale;
    private String formation_Type;
    private String name;

    // Auth Officers
    private Integer ltCapt;
    private Integer maj;
    private Integer ltCol;
    private Integer col;
    private Integer brig;
    private Integer majGen;
    private Integer ltGen;

    // Hard Scale
    private Integer hsLtCapt;
    private Integer hsMaj;
    private Integer hsLtCol;
    private Integer hsCol;
    private Integer hsBrig;
    private Integer hsMajGen;
    private Integer hsLtGen;

}
