package com.healthcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LabTestRequest {

    @NotBlank(message = "Laboratory ID is required")
    private String laboratoryId;

    @NotBlank(message = "Patient ID is required")
    private String patientId;

    @NotBlank(message = "Test requirement is required")
    @Size(min = 10, max = 500, message = "Test requirement must be between 10 and 500 characters")
    private String testRequirement;

    private String notes;
}
