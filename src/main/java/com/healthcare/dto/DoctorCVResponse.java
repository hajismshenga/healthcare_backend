package com.healthcare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorCVResponse {
    
    private String doctorId;
    private String name;
    private String profession;
    private String hospitalName;
    private String hospitalLocation;
    
    // Professional Information
    private String qualifications;
    private String specializations;
    private Integer yearsOfExperience;
    private LocalDate licenseExpiryDate;
    
    // Contact Information
    private String contactNumber;
    private String email;
    
    // Availability
    private String availabilitySchedule;
    private Boolean isAvailableForNewPatients;
    
    // Ratings and Reviews (if implemented later)
    private Double averageRating;
    private Integer totalReviews;
    
    // Additional Information
    private String bio;
    private List<String> languages;
    private String consultationFee;
}
