package com.healthcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DoctorRegistrationRequest {

    @NotBlank(message = "Doctor name is required")
    private String name;

    @NotBlank(message = "Profession is required")
    private String profession;

    @NotBlank(message = "Doctor ID is required")
    @Pattern(regexp = "^DID[-.]\\d{3}$", message = "Doctor ID must be in format 'DID-001' or 'DID.001'")
    private String doctorId;

    @NotBlank(message = "Hospital ID is required")
    private String hospitalId;
}
