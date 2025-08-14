package com.healthcare.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PatientLoginRequest {
    @NotBlank(message = "Patient ID is required")
    private String patientId;
    
    @NotBlank(message = "Password is required")
    private String password;
}
