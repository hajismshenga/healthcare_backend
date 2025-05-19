package com.healthcare.service;

import com.healthcare.model.*;
import com.healthcare.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LabTestService {

    @Autowired
    private LabTestRepository labTestRepository;
    
    @Autowired
    private LabResultRepository labResultRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private LaboratoryRepository laboratoryRepository;
    
    @Autowired
    private ClinicalRecordRepository clinicalRecordRepository;

    @Transactional
    public LabTest requestTest(Long patientId, Long doctorId, Long laboratoryId, String testType, String testDescription, Long clinicalRecordId) {
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        Optional<Laboratory> laboratoryOpt = laboratoryRepository.findById(laboratoryId);
        
        if (!patientOpt.isPresent()) {
            throw new IllegalArgumentException("Patient not found with ID: " + patientId);
        }
        
        if (!doctorOpt.isPresent()) {
            throw new IllegalArgumentException("Doctor not found with ID: " + doctorId);
        }
        
        if (!laboratoryOpt.isPresent()) {
            throw new IllegalArgumentException("Laboratory not found with ID: " + laboratoryId);
        }
        
        ClinicalRecord clinicalRecord = null;
        if (clinicalRecordId != null) {
            Optional<ClinicalRecord> clinicalRecordOpt = clinicalRecordRepository.findById(clinicalRecordId);
            if (!clinicalRecordOpt.isPresent()) {
                throw new IllegalArgumentException("Clinical record not found with ID: " + clinicalRecordId);
            }
            clinicalRecord = clinicalRecordOpt.get();
        }
        
        LabTest test = LabTest.builder()
                .patient(patientOpt.get())
                .requestingDoctor(doctorOpt.get())
                .laboratory(laboratoryOpt.get())
                .testType(testType)
                .testDescription(testDescription)
                .requestedAt(LocalDateTime.now())
                .status("PENDING")
                .clinicalRecord(clinicalRecord)
                .build();
        
        return labTestRepository.save(test);
    }

    public List<LabTest> getAllTests() {
        return labTestRepository.findAll();
    }

    public Optional<LabTest> getTestById(Long id) {
        return labTestRepository.findById(id);
    }
    
    public List<LabTest> getTestsByPatient(Long patientId) {
        return labTestRepository.findByPatientId(patientId);
    }
    
    public List<LabTest> getTestsByDoctor(Long doctorId) {
        return labTestRepository.findByRequestingDoctorId(doctorId);
    }
    
    public List<LabTest> getTestsByLaboratory(Long laboratoryId) {
        return labTestRepository.findByLaboratoryId(laboratoryId);
    }
    
    public List<LabTest> getPendingTestsByLaboratory(Long laboratoryId) {
        return labTestRepository.findByLaboratoryIdAndStatus(laboratoryId, "PENDING");
    }
    
    @Transactional
    public LabResult addTestResult(Long testId, String resultDetails, String interpretation, 
                                   String normalRange, String notes, String recommendations,
                                   String resultsSummaryForPatient) {
        Optional<LabTest> testOpt = labTestRepository.findById(testId);
        
        if (!testOpt.isPresent()) {
            throw new IllegalArgumentException("Lab test not found with ID: " + testId);
        }
        
        LabTest test = testOpt.get();
        test.setStatus("COMPLETED");
        test.setResultsSummaryForPatient(resultsSummaryForPatient);
        labTestRepository.save(test);
        
        LabResult result = LabResult.builder()
                .labTest(test)
                .resultDetails(resultDetails)
                .interpretation(interpretation)
                .normalRange(normalRange)
                .notes(notes)
                .recommendations(recommendations)
                .resultDate(LocalDateTime.now())
                .build();
        
        return labResultRepository.save(result);
    }
    
    public Optional<LabResult> getResultByTestId(Long testId) {
        return labResultRepository.findByLabTestId(testId);
    }
    
    public List<LabResult> getResultsByPatient(Long patientId) {
        return labResultRepository.findByPatientId(patientId);
    }
    
    public List<LabResult> getResultsByDoctor(Long doctorId) {
        return labResultRepository.findByDoctorId(doctorId);
    }
    
    public List<LabResult> getResultsByLaboratory(Long laboratoryId) {
        return labResultRepository.findByLaboratoryId(laboratoryId);
    }
    
    public List<LabTest> getTestsByClinicalRecord(Long clinicalRecordId) {
        return labTestRepository.findByClinicalRecordId(clinicalRecordId);
    }
    
    public List<LabTest> getTestsByPatientAndStatus(Long patientId, String status) {
        return labTestRepository.findByPatientIdAndStatus(patientId, status);
    }
    
    public List<LabResult> getRecentResultsByPatient(Long patientId, int count) {
        List<LabResult> results = labResultRepository.findByPatientId(patientId);
        return results.stream()
                .sorted((r1, r2) -> r2.getResultDate().compareTo(r1.getResultDate()))
                .limit(count)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public LabTest requestTestFromClinicalRecord(Long clinicalRecordId, Long laboratoryId, String testType, String testDescription) {
        Optional<ClinicalRecord> clinicalRecordOpt = clinicalRecordRepository.findById(clinicalRecordId);
        
        if (!clinicalRecordOpt.isPresent()) {
            throw new IllegalArgumentException("Clinical record not found with ID: " + clinicalRecordId);
        }
        
        ClinicalRecord clinicalRecord = clinicalRecordOpt.get();
        return requestTest(
            clinicalRecord.getPatient().getId(),
            clinicalRecord.getDoctor().getId(),
            laboratoryId,
            testType,
            testDescription,
            clinicalRecordId
        );
    }
}
