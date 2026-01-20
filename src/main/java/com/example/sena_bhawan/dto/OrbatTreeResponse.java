package com.example.sena_bhawan.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrbatTreeResponse {
    private Long id;
    private String name;
    private String formationType;
    private String formationCode;
    private List<OrbatTreeResponse> children;
}
