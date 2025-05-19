package com.healthcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String testType;
    private String testDescription;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor requestingDoctor;

    @ManyToOne
    @JoinColumn(name = "laboratory_id")
    private Laboratory laboratory;

    private LocalDateTime requestedAt;

    private String status;

    @ManyToOne
    @JoinColumn(name = "clinical_record_id")
    private ClinicalRecord clinicalRecord;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lab_result_id") // owning side, FK in lab_test table
    private LabResult labResult;

    @Column(length = 1000)
    private String resultsSummaryForPatient;
}
