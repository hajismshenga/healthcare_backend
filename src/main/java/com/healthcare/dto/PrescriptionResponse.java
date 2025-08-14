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
public class PrescriptionResponse {
    
    private String prescriptionId;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private String hospitalId;
    private String hospitalName;
    private String disease;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate prescriptionDate;
    
    private List<Medication> medications;
    private String notes;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate createdAt;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate updatedAt;
}
