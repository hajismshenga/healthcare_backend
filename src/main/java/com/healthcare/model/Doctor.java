package com.healthcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "doctors", schema = "healthcare")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Doctor name is required")
    private String name;

    @NotBlank(message = "Profession is required")
    private String profession;

    @Column(name = "doctor_id", unique = true, nullable = false, updatable = false)
    @Pattern(regexp = "^DID[-.]\\d{3}$", message = "Doctor ID must be in format 'DID-001' or 'DID.001'")
    private String doctorId;

    @NotBlank
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Hospital hospital;
}
