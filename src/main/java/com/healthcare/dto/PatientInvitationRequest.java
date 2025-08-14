package com.healthcare.dto;

import com.healthcare.model.PatientInvitation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientInvitationRequest {
    
    @NotBlank(message = "Doctor ID is required")
    private String doctorId;
    
    @NotBlank(message = "Patient ID is required")
    private String patientId;
    
    @NotBlank(message = "Invitation message is required")
    private String invitationMessage;
    
    @NotNull(message = "Urgency level is required")
    private PatientInvitation.UrgencyLevel urgencyLevel;
    
    private String preferredConsultationDate;
    private String preferredConsultationTime;
}
