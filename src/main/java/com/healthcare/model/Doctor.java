package com.healthcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_id")
    private Long id;

    @Column(name = "doctor_code", unique = true, nullable = false)
    @Pattern(regexp = "^(DID/[0-9]{3}|DID\\.[0-9]{3})$", message = "Doctor code must be in format DID/001 or DID.001")
    private String doctorCode;

    @NotBlank(message = "Doctor name is required")
    private String name;

    @NotBlank(message = "Specialty is required")
    private String specialty;

    @Email(message = "Valid email is required")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Please provide a valid email address")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid")
    private String phoneNumber;

    private String password;

    private LocalDate registrationDate;

    @Builder.Default
    private boolean active = true;

    @ElementCollection
    @Builder.Default
    private List<String> additionalSpecialties = new ArrayList<>();

    private Integer yearsOfExperience;
    private String aboutMe;
    private String profilePictureUrl;
    private String availability;
    private String consultationFee;

    @ElementCollection @Builder.Default private List<String> languagesSpoken = new ArrayList<>();
    @ElementCollection @Builder.Default private List<String> achievements = new ArrayList<>();
    @ElementCollection @Builder.Default private List<String> publications = new ArrayList<>();
    @ElementCollection @Builder.Default private List<String> professionalMemberships = new ArrayList<>();
    @ElementCollection @Builder.Default private List<String> researchInterests = new ArrayList<>();
    @ElementCollection @Builder.Default private List<String> clinicalInterests = new ArrayList<>();
    @ElementCollection @Builder.Default private List<String> teachingExperience = new ArrayList<>();
    @ElementCollection @Builder.Default private List<String> patientReviews = new ArrayList<>();

    private Double averageRating;
    private Integer totalPatientsTreated;

    @Column(length = 2000)
    private String detailedProfile;

    private double rating;

    @ManyToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Transient
    private final String autoPassword = "123456";

    @Transient
    private String qualifications;

    @Transient
    private String experienceYears;

    public void setRegistrationDate(LocalDate date) {
        this.registrationDate = date;
    }

    @Transient
    public String getFormattedExperience() {
        return yearsOfExperience != null ? yearsOfExperience + " years of experience" : "Experience not specified";
    }

    @Transient
    public String getSpecialtiesString() {
        StringBuilder sb = new StringBuilder();
        if (specialty != null) sb.append(specialty);
        if (!additionalSpecialties.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(String.join(", ", additionalSpecialties));
        }
        return sb.toString();
    }

    @Transient
    public String getLanguagesString() {
        return String.join(", ", languagesSpoken);
    }
}
