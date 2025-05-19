package com.healthcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

import lombok.*;

import java.time.LocalDate;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String diagnosis;
    
    @Column(length = 2000)
    private String medications; // JSON list of medications with dosage and instructions
    
    @Column(length = 1000)
    private String instructions; // General instructions for the patient
    
    private Integer durationDays; // Duration of the prescription in days
    
    private LocalDate dateIssued;
    private LocalDate validUntil;
    private LocalDate filledDate; // When the prescription was filled by pharmacy

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    
    // Relationship with clinical record
    @ManyToOne
    @JoinColumn(name = "clinical_record_id")
    private ClinicalRecord clinicalRecord;
    
    // For second opinions
@Builder.Default
    private Boolean isSecondOpinion = false;
    
    @ManyToOne
    @JoinColumn(name = "second_opinion_request_id")
    private SecondOpinionRequest secondOpinionRequest;
    
    // Status: ACTIVE, FILLED, EXPIRED, CANCELLED
@Builder.Default
    private String status = "ACTIVE";
    
    // Refill information
@Builder.Default
    private Integer refillsAllowed = 0;
@Builder.Default
    private Integer refillsRemaining = 0;
    
    // Pharmacy information
    private String pharmacyName;
    private String pharmacyPhone;
    
    // Controlled substance information
@Builder.Default
    private Boolean isControlledSubstance = false;
    private String controlledSubstanceClass; // Schedule I, II, III, IV, V
    
    // For electronic prescriptions
    private String prescriptionCode; // Unique code for e-prescriptions
@Builder.Default
    private Boolean isElectronic = false;
    
    // For tracking if prescription was reviewed by another doctor
@Builder.Default
    private Boolean hasBeenReviewed = false;
    private Long reviewedByDoctorId;
    private LocalDate reviewDate;
    
    @Column(length = 1000)
    private String reviewNotes;
}
