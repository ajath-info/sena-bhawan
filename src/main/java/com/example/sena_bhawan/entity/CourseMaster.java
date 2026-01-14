package com.example.sena_bhawan.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "course_master")
@Getter
@Setter
public class CourseMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "srno")
    private Integer srno;

    @Column(name = "course_name")
    private String courseName;

    @Column(name = "location")
    private String location;

    @Column(name = "duration")
    private Integer duration;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<CourseSchedule> schedules;



}

