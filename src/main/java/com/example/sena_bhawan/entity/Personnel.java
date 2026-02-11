package com.example.sena_bhawan.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "personnel")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Personnel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "commission_type")
    private String commissionType;

    private String armyNo;
    private String rank;
    private String fullName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfCommission;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfSeniority;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    private String placeOfBirth;
    private String officerImage;

    private String nrs;
    private String religion;
    private String aadhaarNumber;
    private String panCard;
    private String maritalStatus;
    private String cdaAccountNo;

    private String permanentAddress;
    private String city;
    private String district;
    private String state;
    private String pinCode;

    private String mobileNumber;
    private String alternateMobile;
    private String emailAddress;
    @Column(name = "nsg_mail")
    private String nsgEmail;

    @Column(name = "medical_category")
    private String medicalCategory;
    @Column(name = "med_date")
    private LocalDate medicalDate;
    @Column(name = "med_category")
    private String injuryCategory;
    @Column(name = "diagnosis")
    private String diagnosis;
    @Column(name = "review_date")
    protected LocalDate reviewDate;
    @Column(name = "restriction")
    private String restriction;
    @Column(name = "irs_transfer")
    private String irsTransfer;



    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // CHILD RELATIONS

    @OneToMany(mappedBy = "personnel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PersonnelDecorations> decorations;

    @OneToMany(mappedBy = "personnel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PersonnelQualifications> qualifications;

    @OneToMany(mappedBy = "personnel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PersonnelAdditionalQualifications> additionalQualifications;

    @OneToMany(mappedBy = "personnel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PersonnelFamily> familyMembers;

    public Personnel(Long personnelId) {
    }
}
