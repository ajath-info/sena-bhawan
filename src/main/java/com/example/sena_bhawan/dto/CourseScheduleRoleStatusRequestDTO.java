package com.example.sena_bhawan.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseScheduleRoleStatusRequestDTO {

    private Long scheduleId;

    private Long roleId;

    private String status;

    private String remark;


}
