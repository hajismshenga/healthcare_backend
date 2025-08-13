package com.healthcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class HospitalRegistrationRequest {
    
    @NotBlank(message = "Hospital name is required")
    @Size(min = 3, max = 100, message = "Hospital name must be between 3 and 100 characters")
    private String name;
    
    @NotBlank(message = "Ownership type is required")
    @Pattern(regexp = "^(?i)(PRIVATE|GOVERNMENT)$", message = "Ownership must be either PRIVATE or GOVERNMENT")
    private String ownership;
    
    @NotBlank(message = "District is required")
    @Size(min = 2, max = 50, message = "District must be between 2 and 50 characters")
    private String district;
    
    @Size(max = 200, message = "Location cannot exceed 200 characters")
    private String location;
    
    @NotBlank(message = "Contact information is required")
    @Size(min = 5, max = 50, message = "Contact information must be between 5 and 50 characters")
    private String contactInfo;
    
    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;
    
    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$", 
             message = "Password must be at least 6 characters long and contain at least one uppercase letter, one lowercase letter, and one number")
    private String password;
}
