package com.example.sena_bhawan.dto;

import jakarta.persistence.Table;

@Table(name = "orbat_structure")
public class SusDropdownDTO {

    private Long susId;
    private String sosNo;
    private String unitName;

    public SusDropdownDTO(Long susId, String sosNo, String unitName) {
        this.susId = susId;
        this.sosNo = sosNo;
        this.unitName = unitName;
    }

    public Long getSusId() {
        return susId;
    }

    public String getSosNo() {
        return sosNo;
    }

    public String getUnitName() {
        return unitName;
    }
}

