package com.healthcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PatientRegistrationRequest {

    @NotBlank(message = "Patient name is required")
    @Size(min = 2, max = 100, message = "Patient name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Patient ID is required")
    @Pattern(regexp = "^PID[-./]\\d{3}$", message = "Patient ID must be in format 'PID-001', 'PID.001', or 'PID/001'")
    private String patientId;

    @NotBlank(message = "Doctor ID is required")
    private String doctorId;
}
