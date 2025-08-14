package com.healthcare.service;

import com.healthcare.dto.LabTestRequest;
import com.healthcare.dto.LabResultSubmissionRequest;
import com.healthcare.exception.DoctorNotFoundException;
import com.healthcare.exception.LaboratoryNotFoundException;
import com.healthcare.exception.PatientNotFoundException;
import com.healthcare.model.LabTest;
import com.healthcare.model.Doctor;
import com.healthcare.model.Laboratory;
import com.healthcare.model.Patient;
import com.healthcare.repository.LabTestRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.LaboratoryRepository;
import com.healthcare.repository.PatientRepository;
import com.healthcare.util.TestIdGenerator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class LabTestService {

    private final LabTestRepository labTestRepository;
    private final DoctorRepository doctorRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final PatientRepository patientRepository;
    private final TestIdGenerator testIdGenerator;

    public LabTestService(LabTestRepository labTestRepository,
                         DoctorRepository doctorRepository,
                         LaboratoryRepository laboratoryRepository,
                         PatientRepository patientRepository,
                         TestIdGenerator testIdGenerator) {
        this.labTestRepository = labTestRepository;
        this.doctorRepository = doctorRepository;
        this.laboratoryRepository = laboratoryRepository;
        this.patientRepository = patientRepository;
        this.testIdGenerator = testIdGenerator;
    }
    
    @Transactional
    public LabTest createLabTest(LabTestRequest request, String doctorId) {
        log.debug("Starting lab test creation for doctor: {}", doctorId);
        
        try {
            // Verify doctor exists
            Doctor doctor = doctorRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> {
                    log.warn("Doctor not found with ID: {}", doctorId);
                    return new DoctorNotFoundException("Doctor not found with ID: " + doctorId);
                });

            // Verify laboratory exists
            Laboratory laboratory = laboratoryRepository.findByLaboratoryId(request.getLaboratoryId())
                .orElseThrow(() -> {
                    log.warn("Laboratory not found with ID: {}", request.getLaboratoryId());
                    return new LaboratoryNotFoundException("Laboratory not found with ID: " + request.getLaboratoryId());
                });

            // Verify patient exists
            Patient patient = patientRepository.findByPatientId(request.getPatientId())
                .orElseThrow(() -> {
                    log.warn("Patient not found with ID: {}", request.getPatientId());
                    return new PatientNotFoundException("Patient not found with ID: " + request.getPatientId());
                });

            // Generate test ID
            String testId = testIdGenerator.generateTestId();
            log.debug("Generated test ID: {}", testId);

            // Create lab test
            LabTest labTest = LabTest.builder()
                    .testId(testId)
                    .laboratory(laboratory)
                    .patient(patient)
                    .doctor(doctor)
                    .testRequirement(request.getTestRequirement())
                    .notes(request.getNotes())
                    .status(LabTest.TestStatus.PENDING)
                    .requestedDate(LocalDateTime.now())
                    .build();

            log.debug("Saving lab test to database: {}", labTest);
            LabTest savedLabTest = labTestRepository.save(labTest);
            log.info("Successfully created lab test with ID: {}", savedLabTest.getTestId());
            
            return savedLabTest;
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while creating lab test: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Could not create lab test. Please check the provided data.");
        } catch (Exception e) {
            log.error("Unexpected error while creating lab test: {}", e.getMessage(), e);
            if (e.getCause() != null) {
                throw new RuntimeException("An unexpected error occurred while creating the lab test: " + e.getCause().getMessage(), e);
            } else {
                throw new RuntimeException("An unexpected error occurred while creating the lab test: " + e.getMessage(), e);
            }
        }
    }

    public Optional<LabTest> findByTestId(String testId) {
        return labTestRepository.findByTestId(testId);
    }

    public List<LabTest> getTestsByDoctor(String doctorId) {
        return labTestRepository.findByDoctorDoctorId(doctorId);
    }
    
    public List<LabTest> getTestsByLaboratory(String laboratoryId) {
        return labTestRepository.findByLaboratoryLaboratoryId(laboratoryId);
    }
    
    public List<LabTest> getTestsByPatient(String patientId) {
        return labTestRepository.findByPatientPatientId(patientId);
    }
    
    public List<LabTest> getTestsByStatus(LabTest.TestStatus status) {
        return labTestRepository.findByStatus(status);
    }
    
    public List<LabTest> getAllTests() {
        log.info("Fetching all lab tests");
        return labTestRepository.findAll();
    }
    
    @Transactional
    public LabTest updateTestStatus(String testId, LabTest.TestStatus status, String notes) {
        LabTest labTest = labTestRepository.findByTestId(testId)
            .orElseThrow(() -> new IllegalArgumentException("Lab test not found with ID: " + testId));
        
        labTest.setStatus(status);
        if (notes != null && !notes.trim().isEmpty()) {
            labTest.setNotes(notes);
        }
        
        if (status == LabTest.TestStatus.COMPLETED) {
            labTest.setCompletedDate(LocalDateTime.now());
        }
        
        return labTestRepository.save(labTest);
    }
    
    @Transactional
    public LabTest addTestResult(String testId, String testResult) {
        LabTest labTest = labTestRepository.findByTestId(testId)
            .orElseThrow(() -> new IllegalArgumentException("Lab test not found with ID: " + testId));
        
        labTest.setTestResult(testResult);
        labTest.setStatus(LabTest.TestStatus.COMPLETED);
        labTest.setCompletedDate(LocalDateTime.now());
        
        return labTestRepository.save(labTest);
    }

    @Transactional
    public LabTest addTestResultByIdentifiers(LabResultSubmissionRequest request) {
        String normalizedPatientId = request.getPatientId();
        if ((normalizedPatientId == null || normalizedPatientId.isBlank()) && request.getPatientName() != null) {
            // Try to find patient by name exactly; if multiple, prefer the most recent pending test
            List<Patient> candidates = patientRepository.findAll().stream()
                    .filter(p -> request.getPatientName().equalsIgnoreCase(p.getName()))
                    .toList();
            if (candidates.isEmpty()) {
                throw new IllegalArgumentException("Patient not found with name: " + request.getPatientName());
            }
            // If multiple, we will search tests per candidate below and pick the first with pending
            for (Patient p : candidates) {
                List<LabTest> tests = labTestRepository.findLatestPendingByPatientAndDoctor(p.getPatientId(), request.getDoctorId());
                if (!tests.isEmpty()) {
                    normalizedPatientId = p.getPatientId();
                    break;
                }
            }
            if (normalizedPatientId == null) {
                throw new IllegalArgumentException("No pending lab test found for the provided patient name and doctor");
            }
        }

        List<LabTest> pendingTests = labTestRepository.findLatestPendingByPatientAndDoctor(normalizedPatientId, request.getDoctorId());
        if (pendingTests.isEmpty()) {
            throw new IllegalArgumentException("No pending lab test found for patient ID: " + normalizedPatientId + " and doctor ID: " + request.getDoctorId());
        }
        LabTest latest = pendingTests.get(0);

        latest.setTestResult(request.getResult());
        latest.setStatus(LabTest.TestStatus.COMPLETED);
        latest.setCompletedDate(LocalDateTime.now());

        return labTestRepository.save(latest);
    }
}
