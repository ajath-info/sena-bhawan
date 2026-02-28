package com.example.sena_bhawan.dto;

import com.example.sena_bhawan.entity.FormationEstablishment;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EstablishmentResponse {

    private Long orbatId;
    private Authorized authorized;
    private HardScale hardScale;
    private FormationEstablishment.EstablishmentType establishmentType;
    private String formationType;
    private String name;

    @Data
    public static class Authorized {
        private Integer totalAuthOfficers;
        private Integer ltCapt;
        private Integer maj;
        private Integer ltCol;
        private Integer col;
        private Integer brig;
        private Integer majGen;
        private Integer ltGen;

    }

    @Data
    public static class HardScale {
        private Integer totalHardScale;
        private Integer ltCapt;
        private Integer maj;
        private Integer ltCol;
        private Integer col;
        private Integer brig;
        private Integer majGen;
        private Integer ltGen;

    }


}

