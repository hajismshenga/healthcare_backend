package com.healthcare.model;

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
    private String name;
    
    @NotBlank(message = "Ownership type is required")
    @Pattern(regexp = "^(PRIVATE|GOVERNMENT)$", message = "Ownership must be either PRIVATE or GOVERNMENT")
    private String ownership;
    
    @NotBlank(message = "District is required")
    @Size(min = 2, max = 50, message = "District must be between 2 and 50 characters")
    private String district;

    @Size(max = 200, message = "Location cannot exceed 200 characters")
    private String location;

    @NotBlank(message = "Contact information is required")
    @Size(min = 5, max = 50, message = "Contact information must be between 5 and 50 characters")
    private String contactInfo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user; // For authentication
    
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Doctor> doctors = new ArrayList<>();
    
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Laboratory> laboratories = new ArrayList<>();
}
