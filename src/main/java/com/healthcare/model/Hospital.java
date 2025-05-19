package com.healthcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List; // ✅ Add this line


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    @Pattern(regexp = "^(HOSP/[0-9]{3}|HOSP\\.[0-9]{3})$", message = "Hospital ID must be in format HOSP/001 or HOSP.001")
    private String hospitalId;

    @NotBlank(message = "Hospital name is required")
    private String name;
    
    @NotBlank(message = "Ownership type is required")
    @Pattern(regexp = "^(PRIVATE|GOVERNMENT)$", message = "Ownership must be either PRIVATE or GOVERNMENT")
    private String ownership;
    
    @NotBlank(message = "District is required")
    private String district;

    private String location;

    @NotBlank(message = "Contact information is required")
    private String contactInfo;

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL)
    private List<Doctor> doctors;

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL)
    private List<Laboratory> laboratories;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user; // For authentication
}
