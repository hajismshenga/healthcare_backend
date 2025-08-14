package com.healthcare.dto;

import com.healthcare.model.PatientInvitation;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorInvitationResponse {
    
    private String invitationId;
    private String patientId;
    private String patientName;
    private String patientAge;
    private String patientGender;
    
    // Invitation Details
    private String invitationMessage;
    private PatientInvitation.UrgencyLevel urgencyLevel;
    private String preferredConsultationDate;
    private String preferredConsultationTime;
    
    // Patient Medical Background
    private List<PatientMedicationHistoryResponse> medicationHistory;
    private String primaryHealthConcern;
    private String allergies;
    private String chronicConditions;
    
    // Invitation Status
    private PatientInvitation.InvitationStatus status;
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime invitationDate;
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime responseDate;
    
    private String doctorResponse;
}
