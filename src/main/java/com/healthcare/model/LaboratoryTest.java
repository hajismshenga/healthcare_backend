package com.healthcare.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LaboratoryTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String testType;  // Type of test, e.g., "Blood Test", "X-ray"
    private String testResults;
    private LocalDate testDate;

    // Link to Doctor and Patient
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    // Link to Laboratory (optional, if you want to track which laboratory is processing the test)
    @ManyToOne
    @JoinColumn(name = "laboratory_id")
    private Laboratory laboratory;
}
