package com.healthcare.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "patient", schema = "healthcare")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "patient_id", unique = true, nullable = false, updatable = false)
    @Pattern(regexp = "^PID[-./]\\d{3}$", message = "Patient ID must be in format 'PID-001', 'PID.001', or 'PID/001'")
    private String patientId;

    @NotBlank(message = "Patient name is required")
    @Size(min = 2, max = 100, message = "Patient name must be between 2 and 100 characters")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "Password is required")
    @Column(name = "password")
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference("doctor-patients")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference("hospital-patients")
    private Hospital hospital;
}
