package com.healthcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "laboratory", schema = "healthcare")
public class Laboratory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "laboratory_id", unique = true, nullable = false, updatable = false)
    @Pattern(regexp = "^LAB[-./]\\d{3}$", message = "Laboratory ID must be in format 'LAB-001', 'LAB.001', or 'LAB/001'")
    private String laboratoryId;

    @NotBlank(message = "Laboratory name is required")
    @Size(min = 3, max = 100, message = "Laboratory name must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Password is required")
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Hospital hospital;
}
