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
@Table(name = "hospital", schema = "healthcare")
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "hospital_id", unique = true, nullable = false, updatable = false)
    private String hospitalId;

    @NotBlank(message = "Hospital name is required")
    @Size(min = 3, max = 100, message = "Hospital name must be between 3 and 100 characters")
    @Column(name = "name")
    private String name;
    
    @NotBlank(message = "Ownership type is required")
    @Pattern(regexp = "^(PRIVATE|GOVERNMENT)$", message = "Ownership must be either PRIVATE or GOVERNMENT")
    @Column(name = "ownership")
    private String ownership;
    
    @NotBlank(message = "District is required")
    @Size(min = 2, max = 50, message = "District must be between 2 and 50 characters")
    @Column(name = "district")
    private String district;

    @Size(max = 200, message = "Location cannot exceed 200 characters")
    @Column(name = "location")
    private String location;

    @NotBlank(message = "Contact information is required")
    @Size(min = 5, max = 50, message = "Contact information must be between 5 and 50 characters")
    @Column(name = "contact_info")
    private String contactInfo;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference("user-hospital")
    private User user; // For authentication
    
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    @JsonManagedReference("hospital-doctors")
    private List<Doctor> doctors = new ArrayList<>();
    
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    @JsonManagedReference("hospital-labs")
    private List<Laboratory> laboratories = new ArrayList<>();
    
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    @JsonManagedReference("hospital-patients")
    private List<Patient> patients = new ArrayList<>();
}
