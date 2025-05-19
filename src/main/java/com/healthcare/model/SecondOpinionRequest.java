package com.healthcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecondOpinionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "requesting_doctor_id")
    private Doctor requestingDoctor; // Original doctor

    @ManyToOne
    @JoinColumn(name = "consulting_doctor_id")
    private Doctor consultingDoctor; // Doctor providing second opinion

    @Column(length = 2000)
    private String conditionDescription;
    
    @Column(length = 2000)
    private String currentTreatment;
    
    @Column(length = 3000)
    private String secondOpinion; // Advice from consulting doctor
    
    @Column(length = 1000)
    private String recommendedMedication;
    
    @Column(length = 1000)
    private String recommendedTests;
    
    private LocalDateTime requestDate;
    private LocalDateTime responseDate;
    @Column(name = "completion_date")
    private LocalDateTime completionDate;
    
    @Column(name = "is_urgent")
    @Builder.Default
    private boolean isUrgent = false;
    
    // PENDING, IN_REVIEW, COMPLETED, CANCELLED, REJECTED
    private String status;
    
    @Column(length = 1000)
    private String notes;
    
    @OneToMany(mappedBy = "secondOpinionRequest", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Prescription> resultingPrescriptions = new ArrayList<>();
    
    // Reference to the clinical record that initiated this second opinion request
    @ManyToOne
    @JoinColumn(name = "clinical_record_id")
    private ClinicalRecord clinicalRecord;
}