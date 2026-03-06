package com.example.sena_bhawan.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class PersonnelFullDetailsDTO {

    private Long personnelId;

    private List<PostingDetailsDTO> postingDetails;

    private List<PostingRequestDTO> postingRequest;

    private List<CourseDetailsRequestDTO> courseDetails;
}
