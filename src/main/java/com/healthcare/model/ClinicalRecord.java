package com.healthcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    
    private LocalDate visitDate;
    
    @Column(length = 1000)
    private String symptoms;
    
    @Column(length = 1000)
    private String diagnosis;
    
    @Column(length = 2000)
    private String treatmentPlan;
    
    @Column(length = 2000)
    private String notes;
    
    // Vital signs
    private Double temperature;
    private Integer heartRate;
    private Integer respiratoryRate;
    private String bloodPressure;
    private Double weight;
    private Double height;
    private Double bmi;
    
    @Column(length = 1000)
    private String allergies;
    
    @Column(length = 1000)
    private String currentMedications;
    
    @Column(length = 1000)
    private String labResults;
    
    @Column(length = 1000)
    private String imagingResults;
    
    private Boolean requiresFollowUp;
    private LocalDate followUpDate;
    
    @Column(length = 1000)
    private String followUpInstructions;
    
    // Relationship with prescriptions
    @OneToMany(mappedBy = "clinicalRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Prescription> prescriptions = new ArrayList<>();
    
    // Record type (e.g., INITIAL_VISIT, FOLLOW_UP, EMERGENCY, SPECIALIST_CONSULTATION)
    private String recordType;
    
    // Severity level (e.g., LOW, MEDIUM, HIGH, CRITICAL)
    private String severityLevel;
    
    // For tracking if the record has been reviewed by another doctor
    @Builder.Default
    private Boolean hasBeenReviewed = false;
    private Long reviewedByDoctorId;
    private LocalDate reviewDate;
    
    @Column(length = 1000)
    private String reviewNotes;

    public void setMedicationPrescribed(String medication) {
        // Add medication to prescriptions
        if (prescriptions == null) {
            prescriptions = new ArrayList<>();
        }
        Prescription prescription = new Prescription();
        prescription.setMedications(medication);
        prescription.setClinicalRecord(this);
        prescriptions.add(prescription);
    }


}
