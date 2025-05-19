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
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String patientId; // Generated patient ID

    private String name;
    
    private Integer age;
    
    private String gender;
    
    private String phoneNumber;
    
    private String address;
    
    private LocalDate registrationDate;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor treatingDoctor;
    
    private String clinicalSummary; // Patient's clinical information
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user; // For authentication
}
