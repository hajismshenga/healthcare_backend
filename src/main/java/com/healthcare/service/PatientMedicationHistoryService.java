package com.healthcare.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.dto.Medication;
import com.healthcare.dto.PatientMedicationHistoryResponse;
import com.healthcare.exception.PatientNotFoundException;
import com.healthcare.model.Prescription;
import com.healthcare.repository.PatientRepository;
import com.healthcare.repository.PrescriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PatientMedicationHistoryService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public PatientMedicationHistoryService(PrescriptionRepository prescriptionRepository,
                                         PatientRepository patientRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.patientRepository = patientRepository;
        this.objectMapper = new ObjectMapper();
    }

    public List<PatientMedicationHistoryResponse> getPatientMedicationHistory(String patientId) {
        log.info("Fetching medication history for patient: {}", patientId);
        
        // Verify patient exists
        if (!patientRepository.existsByPatientId(patientId)) {
            throw new PatientNotFoundException("Patient not found with ID: " + patientId);
        }
        
        List<Prescription> prescriptions = prescriptionRepository.findByPatientPatientId(patientId);
        return prescriptions.stream()
                .map(this::convertToPatientHistoryResponse)
                .collect(Collectors.toList());
    }

    public List<PatientMedicationHistoryResponse> getPatientMedicationHistoryByDateRange(
            String patientId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching medication history for patient: {} between {} and {}", 
                patientId, startDate, endDate);
        
        // Verify patient exists
        if (!patientRepository.existsByPatientId(patientId)) {
            throw new PatientNotFoundException("Patient not found with ID: " + patientId);
        }
        
        List<Prescription> prescriptions = prescriptionRepository
                .findByPatientPatientIdAndPrescriptionDateBetween(patientId, startDate, endDate);
        return prescriptions.stream()
                .map(this::convertToPatientHistoryResponse)
                .collect(Collectors.toList());
    }

    public PatientMedicationHistoryResponse getPatientMedicationHistoryById(String patientId, String prescriptionId) {
        log.info("Fetching specific medication history for patient: {} with prescription: {}", 
                patientId, prescriptionId);
        
        // Verify patient exists
        if (!patientRepository.existsByPatientId(patientId)) {
            throw new PatientNotFoundException("Patient not found with ID: " + patientId);
        }
        
        return prescriptionRepository.findByPrescriptionId(prescriptionId)
                .filter(prescription -> prescription.getPatient().getPatientId().equals(patientId))
                .map(this::convertToPatientHistoryResponse)
                .orElse(null);
    }

    private PatientMedicationHistoryResponse convertToPatientHistoryResponse(Prescription prescription) {
        List<Medication> medications;
        try {
            if (prescription.getMedications() != null && !prescription.getMedications().trim().isEmpty()) {
                medications = objectMapper.readValue(prescription.getMedications(), 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Medication.class));
            } else {
                medications = List.of();
            }
        } catch (JsonProcessingException e) {
            log.error("Error parsing medications JSON: {}", e.getMessage());
            medications = List.of();
        }

        return PatientMedicationHistoryResponse.builder()
                .prescriptionId(prescription.getPrescriptionId())
                .prescriptionDate(prescription.getPrescriptionDate())
                .doctorName(prescription.getDoctor().getName())
                .hospitalName(prescription.getHospital().getName())
                .disease(prescription.getDisease())
                .medications(medications)
                .notes(prescription.getNotes())
                .createdAt(prescription.getCreatedAt())
                .build();
    }
}
