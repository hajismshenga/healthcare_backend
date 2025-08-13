package com.healthcare.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LaboratoryLoginRequest {
    @NotBlank(message = "Laboratory ID is required")
    private String laboratoryId;
    
    @NotBlank(message = "Password is required")
    private String password;
}
