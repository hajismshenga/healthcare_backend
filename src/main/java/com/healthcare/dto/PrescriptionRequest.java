package com.healthcare.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class PrescriptionRequest {
    
    @NotBlank(message = "Patient ID is required")
    private String patientId;
    
    @NotBlank(message = "Disease is required")
    private String disease;
    
    @NotNull(message = "Prescription date is required")
    private LocalDate prescriptionDate;
    
    @NotEmpty(message = "At least one medication is required")
    @Valid
    private List<Medication> medications;
    
    private String notes; // Optional notes
}
