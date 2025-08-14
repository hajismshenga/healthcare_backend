package com.healthcare.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "doctors", schema = "healthcare")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Doctor name is required")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "Profession is required")
    @Column(name = "profession")
    private String profession;

    @Column(name = "doctor_id", unique = true, nullable = false, updatable = false)
    @Pattern(regexp = "^DID[-.]\\d{3}$", message = "Doctor ID must be in format 'DID-001' or 'DID.001'")
    private String doctorId;

    @NotBlank
    @Column(name = "password")
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference("hospital-doctors")
    private Hospital hospital;
    
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference("user-doctor")
    private User user;
    
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    @JsonManagedReference("doctor-patients")
    private List<Patient> patients = new ArrayList<>();
}
