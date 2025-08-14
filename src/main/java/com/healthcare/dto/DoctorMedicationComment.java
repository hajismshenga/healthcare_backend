package com.healthcare.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorMedicationComment {
    
    private String commentId;
    
    @NotBlank(message = "Prescription ID is required")
    private String prescriptionId;
    
    @NotBlank(message = "Doctor ID is required")
    private String doctorId;
    
    @NotBlank(message = "Patient ID is required")
    private String patientId;
    
    @NotBlank(message = "Comment is required")
    private String comment;
    
    @NotNull(message = "Comment type is required")
    private com.healthcare.model.DoctorMedicationComment.CommentType commentType;
    
    private String recommendations;
    private String sideEffects;
    private String dosageAdjustments;
    private String alternativeMedications;
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime commentDate;
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime lastUpdated;
}
