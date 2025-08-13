package com.healthcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LaboratoryRegistrationRequest {

    @NotBlank(message = "Laboratory name is required")
    @Size(min = 3, max = 100, message = "Laboratory name must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Laboratory ID is required")
    @Pattern(regexp = "^LAB[-./]\\d{3}$", message = "Laboratory ID must be in format 'LAB-001', 'LAB.001', or 'LAB/001'")
    private String laboratoryId;

    @NotBlank(message = "Hospital ID is required")
    private String hospitalId;
}
