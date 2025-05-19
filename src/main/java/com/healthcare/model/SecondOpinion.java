package com.healthcare.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecondOpinion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
    
    @ManyToOne
    @JoinColumn(name = "requesting_doctor_id")
    private Doctor requestingDoctor;
    
    @ManyToOne
    @JoinColumn(name = "reviewing_doctor_id")
    private Doctor reviewingDoctor;
    
    private LocalDate requestDate;
    
    @Column(length = 2000)
    private String caseSummary;
    
    @Column(length = 2000)
    private String currentTreatment;
    
    @Column(length = 2000)
    private String additionalInformation;
    
    @Column(length = 2000)
    private String reviewingDoctorNotes;
    
    @Column(length = 1000)
    private String recommendedMedications;
    
    @Column(length = 1000)
    private String recommendedTests;
    
    @Column(length = 2000)
    private String additionalRecommendations;
    
    @Enumerated(EnumType.STRING)
    private SecondOpinionStatus status;
    
    public enum SecondOpinionStatus {
        PENDING, IN_REVIEW, COMPLETED, REJECTED
    }
}
