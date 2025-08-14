package com.healthcare.service;

import com.healthcare.dto.DoctorInvitationResponse;
import com.healthcare.dto.PatientInvitationRequest;
import com.healthcare.exception.DoctorNotFoundException;
import com.healthcare.exception.PatientNotFoundException;
import com.healthcare.model.Doctor;
import com.healthcare.model.Patient;
import com.healthcare.model.PatientInvitation;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.PatientInvitationRepository;
import com.healthcare.repository.PatientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PatientInvitationService {

    private final PatientInvitationRepository patientInvitationRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PatientMedicationHistoryService patientMedicationHistoryService;

    @Autowired
    public PatientInvitationService(PatientInvitationRepository patientInvitationRepository,
                                  PatientRepository patientRepository,
                                  DoctorRepository doctorRepository,
                                  PatientMedicationHistoryService patientMedicationHistoryService) {
        this.patientInvitationRepository = patientInvitationRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.patientMedicationHistoryService = patientMedicationHistoryService;
    }

    public String createInvitation(PatientInvitationRequest request) {
        log.info("Creating invitation from patient: {} to doctor: {}", request.getPatientId(), request.getDoctorId());
        
        // Verify patient exists
        Patient patient = patientRepository.findByPatientId(request.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + request.getPatientId()));
        
        // Verify doctor exists
        Doctor doctor = doctorRepository.findByDoctorId(request.getDoctorId())
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with ID: " + request.getDoctorId()));
        
        // Generate invitation ID
        String invitationId = generateInvitationId();
        
        // Set expiry date (7 days from now)
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);
        
        // Create invitation
        PatientInvitation invitation = PatientInvitation.builder()
                .invitationId(invitationId)
                .patient(patient)
                .doctor(doctor)
                .invitationMessage(request.getInvitationMessage())
                .urgencyLevel(request.getUrgencyLevel())
                .preferredConsultationDate(request.getPreferredConsultationDate())
                .preferredConsultationTime(request.getPreferredConsultationTime())
                .status(PatientInvitation.InvitationStatus.PENDING)
                .expiryDate(expiryDate)
                .build();
        
        PatientInvitation savedInvitation = patientInvitationRepository.save(invitation);
        log.info("Successfully created invitation with ID: {}", invitationId);
        
        return invitationId;
    }

    public List<DoctorInvitationResponse> getDoctorInvitations(String doctorId) {
        log.info("Fetching invitations for doctor: {}", doctorId);
        
        List<PatientInvitation> invitations = patientInvitationRepository
                .findByDoctorDoctorIdAndStatus(doctorId, PatientInvitation.InvitationStatus.PENDING);
        
        return invitations.stream()
                .map(this::convertToDoctorInvitationResponse)
                .collect(Collectors.toList());
    }

    public void respondToInvitation(String invitationId, String response, boolean accepted) {
        log.info("Doctor responding to invitation: {} with response: {}, accepted: {}", invitationId, response, accepted);
        
        PatientInvitation invitation = patientInvitationRepository.findByInvitationId(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found with ID: " + invitationId));
        
        if (invitation.getStatus() != PatientInvitation.InvitationStatus.PENDING) {
            throw new IllegalStateException("Invitation is no longer pending");
        }
        
        invitation.setDoctorResponse(response);
        invitation.setResponseDate(LocalDateTime.now());
        invitation.setStatus(accepted ? 
                PatientInvitation.InvitationStatus.ACCEPTED : 
                PatientInvitation.InvitationStatus.DECLINED);
        
        patientInvitationRepository.save(invitation);
        log.info("Successfully updated invitation status to: {}", invitation.getStatus());
    }

    public List<DoctorInvitationResponse> getPatientInvitations(String patientId) {
        log.info("Fetching invitations for patient: {}", patientId);
        
        List<PatientInvitation> invitations = patientInvitationRepository.findByPatientPatientId(patientId);
        
        return invitations.stream()
                .map(this::convertToDoctorInvitationResponse)
                .collect(Collectors.toList());
    }

    private String generateInvitationId() {
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "INV/" + today + "/";
        
        String maxId = patientInvitationRepository.findMaxInvitationId(prefix);
        
        if (maxId == null) {
            return prefix + "001";
        }
        
        try {
            String[] parts = maxId.split("/");
            if (parts.length >= 3) {
                int sequence = Integer.parseInt(parts[2]);
                return String.format("%s%03d", prefix, sequence + 1);
            }
        } catch (NumberFormatException e) {
            log.warn("Error parsing invitation ID sequence: {}", maxId);
        }
        
        return prefix + "001";
    }

    private DoctorInvitationResponse convertToDoctorInvitationResponse(PatientInvitation invitation) {
        // Get patient medication history for this invitation
        List<com.healthcare.dto.PatientMedicationHistoryResponse> medicationHistory = 
                patientMedicationHistoryService.getPatientMedicationHistory(invitation.getPatient().getPatientId());
        
        return DoctorInvitationResponse.builder()
                .invitationId(invitation.getInvitationId())
                .patientId(invitation.getPatient().getPatientId())
                .patientName(invitation.getPatient().getName())
                .patientAge("N/A") // Could be calculated from patient data if available
                .patientGender("N/A") // Could be added to patient model if needed
                .invitationMessage(invitation.getInvitationMessage())
                .urgencyLevel(invitation.getUrgencyLevel())
                .preferredConsultationDate(invitation.getPreferredConsultationDate())
                .preferredConsultationTime(invitation.getPreferredConsultationTime())
                .medicationHistory(medicationHistory)
                .primaryHealthConcern("N/A") // Could be added to invitation if needed
                .allergies("N/A") // Could be added to patient model if needed
                .chronicConditions("N/A") // Could be added to patient model if needed
                .status(invitation.getStatus())
                .invitationDate(invitation.getInvitationDate())
                .responseDate(invitation.getResponseDate())
                .doctorResponse(invitation.getDoctorResponse())
                .build();
    }
}
