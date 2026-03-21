package com.example.sena_bhawan.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Column(name = "army_no", nullable = false)
    private String armyNo;

    @Column(name = "rank", nullable = false)
    private String rank;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "gender")
    private String gender;

    @Column(name = "case_type")
    private String caseType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_of_commission", nullable = false)
    private LocalDate dateOfCommission;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_of_seniority")
    private LocalDate dateOfSeniority;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "place_of_birth")
    private String placeOfBirth;

    @Column(name = "officer_image")
    private String officerImage;

    @Column(name = "nrs")
    private String nrs;

    @Column(name = "nearest_airport")
    private String nearestAirport;

    @Column(name = "religion")
    private String religion;

    @Column(name = "aadhaar_number", nullable = false)
    private String aadhaarNumber;

    @Column(name = "pan_card", nullable = false)
    private String panCard;

    @Column(name = "marital_status")
    private String maritalStatus;

    @Column(name = "cda_account_no")
    private String cdaAccountNo;

    @Column(name = "permanent_address", nullable = false)
    private String permanentAddress;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "district")
    private String district;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "pin_code", nullable = false)
    private String pinCode;

    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;

    @Column(name = "alternate_mobile")
    private String alternateMobile;

    @Column(name = "email_address", nullable = false)
    private String emailAddress;

    @Column(name = "nic_email")
    private String nicEmail;

    @Column(name = "medical_code", length = 100)
    private String medicalCode;

    @Column(name = "medical_values_s")
    private String medicalValuesS;

    @Column(name = "medical_values_h")
    private String medicalValuesH;

    @Column(name = "medical_values_a")
    private String medicalValuesA;

    @Column(name = "medical_values_p")
    private String medicalValuesP;

    @Column(name = "medical_values_e")
    private String medicalValuesE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Child Relations
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
    private List<PersonnelSports> sports;

    @OneToMany(mappedBy = "personnel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PersonnelFamily> familyMembers;

    @OneToMany(mappedBy = "personnel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PersonnelMedicalDetails> medicalDetails;
}