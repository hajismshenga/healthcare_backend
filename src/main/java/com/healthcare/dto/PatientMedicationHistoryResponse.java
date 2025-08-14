package com.healthcare.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class PatientMedicationHistoryResponse {
    
    private String prescriptionId;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate prescriptionDate;
    
    private String doctorName;
    private String hospitalName;
    private String disease;
    private List<Medication> medications;
    private String notes;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate createdAt;
}
