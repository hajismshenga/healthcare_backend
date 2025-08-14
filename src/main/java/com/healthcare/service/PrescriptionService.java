package com.healthcare.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.dto.Medication;
import com.healthcare.dto.PrescriptionRequest;
import com.healthcare.dto.PrescriptionResponse;
import com.healthcare.exception.DoctorNotFoundException;
import com.healthcare.exception.HospitalNotFoundException;
import com.healthcare.exception.PatientNotFoundException;
import com.healthcare.model.Doctor;
import com.healthcare.model.Hospital;
import com.healthcare.model.Patient;
import com.healthcare.model.Prescription;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.HospitalRepository;
import com.healthcare.repository.PatientRepository;
import com.healthcare.repository.PrescriptionRepository;
import com.healthcare.util.IdGenerator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final IdGenerator idGenerator;
    private final ObjectMapper objectMapper;

    @Autowired
    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                             PatientRepository patientRepository,
                             DoctorRepository doctorRepository,
                             HospitalRepository hospitalRepository,
                             IdGenerator idGenerator) {
        this.prescriptionRepository = prescriptionRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.hospitalRepository = hospitalRepository;
        this.idGenerator = idGenerator;
        this.objectMapper = new ObjectMapper();
    }

    @Transactional
    public PrescriptionResponse createPrescription(PrescriptionRequest request, String doctorId) {
        log.info("Creating prescription for patient: {} by doctor: {}", request.getPatientId(), doctorId);
        
        // Verify doctor exists
        Doctor doctor = doctorRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with ID: " + doctorId));
        
        // Verify patient exists
        Patient patient = patientRepository.findByPatientId(request.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + request.getPatientId()));
        
        // Get hospital from doctor
        Hospital hospital = doctor.getHospital();
        if (hospital == null) {
            throw new HospitalNotFoundException("Doctor is not associated with any hospital");
        }
        
        // Generate prescription ID
        String prescriptionId = generatePrescriptionId();
        
        // Convert medications to JSON string
        String medicationsJson;
        try {
            medicationsJson = objectMapper.writeValueAsString(request.getMedications());
        } catch (JsonProcessingException e) {
            log.error("Error converting medications to JSON: {}", e.getMessage());
            throw new RuntimeException("Error processing medications");
        }
        
        // Create prescription
        Prescription prescription = Prescription.builder()
                .prescriptionId(prescriptionId)
                .patient(patient)
                .doctor(doctor)
                .hospital(hospital)
                .disease(request.getDisease())
                .prescriptionDate(request.getPrescriptionDate())
                .medications(medicationsJson)
                .notes(request.getNotes())
                .build();
        
        Prescription savedPrescription = prescriptionRepository.save(prescription);
        log.info("Successfully created prescription with ID: {}", prescriptionId);
        
        return convertToResponse(savedPrescription);
    }

    public List<PrescriptionResponse> getPrescriptionsByPatient(String patientId) {
        log.info("Fetching prescriptions for patient: {}", patientId);
        List<Prescription> prescriptions = prescriptionRepository.findByPatientPatientId(patientId);
        return prescriptions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<PrescriptionResponse> getPrescriptionsByDoctor(String doctorId) {
        log.info("Fetching prescriptions by doctor: {}", doctorId);
        List<Prescription> prescriptions = prescriptionRepository.findByDoctorDoctorId(doctorId);
        return prescriptions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<PrescriptionResponse> getPrescriptionsByHospital(String hospitalId) {
        log.info("Fetching prescriptions for hospital: {}", hospitalId);
        List<Prescription> prescriptions = prescriptionRepository.findByHospitalHospitalId(hospitalId);
        return prescriptions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Optional<PrescriptionResponse> getPrescriptionById(String prescriptionId) {
        log.info("Fetching prescription by ID: {}", prescriptionId);
        return prescriptionRepository.findByPrescriptionId(prescriptionId)
                .map(this::convertToResponse);
    }

    public List<PrescriptionResponse> getAllPrescriptions() {
        log.info("Fetching all prescriptions");
        List<Prescription> prescriptions = prescriptionRepository.findAll();
        return prescriptions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<PrescriptionResponse> getPrescriptionsByDateRange(String patientId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching prescriptions for patient: {} between {} and {}", patientId, startDate, endDate);
        List<Prescription> prescriptions = prescriptionRepository.findByPatientPatientIdAndPrescriptionDateBetween(patientId, startDate, endDate);
        return prescriptions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private String generatePrescriptionId() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "PRESC/" + today + "/";
        
        String maxId = prescriptionRepository.findMaxPrescriptionId(prefix);
        
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
            log.warn("Error parsing prescription ID sequence: {}", maxId);
        }
        
        return prefix + "001";
    }

    private PrescriptionResponse convertToResponse(Prescription prescription) {
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

        return PrescriptionResponse.builder()
                .prescriptionId(prescription.getPrescriptionId())
                .patientId(prescription.getPatient().getPatientId())
                .patientName(prescription.getPatient().getName())
                .doctorId(prescription.getDoctor().getDoctorId())
                .doctorName(prescription.getDoctor().getName())
                .hospitalId(prescription.getHospital().getHospitalId())
                .hospitalName(prescription.getHospital().getName())
                .disease(prescription.getDisease())
                .prescriptionDate(prescription.getPrescriptionDate())
                .medications(medications)
                .notes(prescription.getNotes())
                .createdAt(prescription.getCreatedAt())
                .updatedAt(prescription.getUpdatedAt())
                .build();
    }
}
