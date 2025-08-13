package com.healthcare.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DoctorLoginRequest {
    @NotBlank(message = "Doctor ID is required")
    private String doctorId;
    
    @NotBlank(message = "Password is required")
    private String password;
}
