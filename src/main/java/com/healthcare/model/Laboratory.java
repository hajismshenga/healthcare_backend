package com.healthcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Laboratory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    @Pattern(regexp = "^(LAB/[0-9]{3}|LAB\\.[0-9]{3})$", message = "Laboratory ID must be in format LAB/001 or LAB.001")
    private String labId;
    
    @NotBlank(message = "Laboratory name is required")
    private String name;
    
    @NotBlank(message = "Specialization is required")
    private String specialization; // Type of lab
    
    @ManyToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user; // For authentication
}
