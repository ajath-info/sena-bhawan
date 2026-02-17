package com.example.sena_bhawan.dto;

import com.example.sena_bhawan.entity.UnitEstablishment;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnitEstablishmentResponse {

    private Long unitId;
    private Authorized authorized;
    private HardScale hardScale;
    private UnitEstablishment.EstablishmentType establishmentType;

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

