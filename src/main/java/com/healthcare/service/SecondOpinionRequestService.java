package com.healthcare.service;

import com.healthcare.model.ClinicalRecord;
import com.healthcare.model.Doctor;
import com.healthcare.model.Patient;
import com.healthcare.model.SecondOpinionRequest;
import com.healthcare.repository.ClinicalRecordRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.PatientRepository;
import com.healthcare.repository.SecondOpinionRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SecondOpinionRequestService {

    @Autowired
    private SecondOpinionRequestRepository repository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private ClinicalRecordRepository clinicalRecordRepository;

    @Transactional
    public SecondOpinionRequest createSecondOpinionRequest(
            Long patientId, 
            Long requestingDoctorId, 
            Long consultingDoctorId, 
            String conditionDescription, 
            String currentTreatment) {
        
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        Optional<Doctor> requestingDoctorOpt = doctorRepository.findById(requestingDoctorId);
        Optional<Doctor> consultingDoctorOpt = doctorRepository.findById(consultingDoctorId);
        
        if (!patientOpt.isPresent()) {
            throw new IllegalArgumentException("Patient not found with ID: " + patientId);
        }
        
        if (!requestingDoctorOpt.isPresent()) {
            throw new IllegalArgumentException("Requesting doctor not found with ID: " + requestingDoctorId);
        }
        
        if (!consultingDoctorOpt.isPresent()) {
            throw new IllegalArgumentException("Consulting doctor not found with ID: " + consultingDoctorId);
        }
        
        SecondOpinionRequest request = new SecondOpinionRequest();
        request.setPatient(patientOpt.get());
        request.setRequestingDoctor(requestingDoctorOpt.get());
        request.setConsultingDoctor(consultingDoctorOpt.get());
        request.setConditionDescription(conditionDescription);
        request.setCurrentTreatment(currentTreatment);
        request.setRequestDate(LocalDateTime.now());
        request.setStatus("PENDING");
        
        return repository.save(request);
    }
    
    public SecondOpinionRequest createRequest(SecondOpinionRequest request) {
        if (request.getRequestDate() == null) {
            request.setRequestDate(LocalDateTime.now());
        }
        
        if (request.getStatus() == null || request.getStatus().isEmpty()) {
            request.setStatus("PENDING");
        }
        
        return repository.save(request);
    }

    public Optional<SecondOpinionRequest> getRequestById(Long id) {
        return repository.findById(id);
    }

    public List<SecondOpinionRequest> getAllRequests() {
        return repository.findAll();
    }
    
    public Page<SecondOpinionRequest> getAllRequestsPaginated(Pageable pageable) {
        return repository.findAll(pageable);
    }
    
    public List<SecondOpinionRequest> getRequestsByPatient(Long patientId) {
        return repository.findByPatientId(patientId);
    }
    
    public Page<SecondOpinionRequest> getRequestsByPatientPaginated(Long patientId, Pageable pageable) {
        return repository.findByPatientId(patientId, pageable);
    }
    
    public List<SecondOpinionRequest> getRequestsByPatientAndStatus(Long patientId, String status) {
        return repository.findByPatientIdAndStatus(patientId, status);
    }
    
    public List<SecondOpinionRequest> getRequestsByConsultingDoctor(Long doctorId) {
        return repository.findByConsultingDoctorId(doctorId);
    }
    
    public Page<SecondOpinionRequest> getRequestsByConsultingDoctorPaginated(Long doctorId, Pageable pageable) {
        return repository.findByConsultingDoctorId(doctorId, pageable);
    }
    
    public List<SecondOpinionRequest> getRequestsByConsultingDoctorAndStatus(Long doctorId, String status) {
        return repository.findByConsultingDoctorIdAndStatus(doctorId, status);
    }
    
    public List<SecondOpinionRequest> getRequestsByRequestingDoctor(Long doctorId) {
        return repository.findByRequestingDoctorId(doctorId);
    }
    
    public Page<SecondOpinionRequest> getRequestsByRequestingDoctorPaginated(Long doctorId, Pageable pageable) {
        return repository.findByRequestingDoctorId(doctorId, pageable);
    }
    
    public List<SecondOpinionRequest> getRequestsByRequestingDoctorAndStatus(Long doctorId, String status) {
        return repository.findByRequestingDoctorIdAndStatus(doctorId, status);
    }
    
    public List<SecondOpinionRequest> getRequestsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findByRequestDateBetween(startDate, endDate);
    }
    
    public List<SecondOpinionRequest> searchRequests(String keyword) {
        return repository.searchByKeyword(keyword);
    }

    @Transactional
    public SecondOpinionRequest updateRequest(Long id, SecondOpinionRequest updatedRequest) {
        Optional<SecondOpinionRequest> existingRequestOpt = repository.findById(id);
        
        if (!existingRequestOpt.isPresent()) {
            throw new IllegalArgumentException("Second opinion request not found with ID: " + id);
        }
        
        SecondOpinionRequest existingRequest = existingRequestOpt.get();
        
        // Update fields but preserve relationships
        existingRequest.setConditionDescription(updatedRequest.getConditionDescription());
        existingRequest.setCurrentTreatment(updatedRequest.getCurrentTreatment());
        
        if (updatedRequest.getSecondOpinion() != null) {
            existingRequest.setSecondOpinion(updatedRequest.getSecondOpinion());
        }
        
        if (updatedRequest.getStatus() != null) {
            existingRequest.setStatus(updatedRequest.getStatus());
        }
        
        return repository.save(existingRequest);
    }
    
    @Transactional
    public SecondOpinionRequest provideSecondOpinion(Long id, String secondOpinion) {
        Optional<SecondOpinionRequest> requestOpt = repository.findById(id);
        
        if (!requestOpt.isPresent()) {
            throw new IllegalArgumentException("Second opinion request not found with ID: " + id);
        }
        
        SecondOpinionRequest request = requestOpt.get();
        request.setSecondOpinion(secondOpinion);
        request.setStatus("COMPLETED");
        request.setCompletionDate(LocalDateTime.now());
        
        return repository.save(request);
    }
    
    @Transactional
    public SecondOpinionRequest updateRequestStatus(Long id, String status) {
        Optional<SecondOpinionRequest> requestOpt = repository.findById(id);
        
        if (!requestOpt.isPresent()) {
            throw new IllegalArgumentException("Second opinion request not found with ID: " + id);
        }
        
        SecondOpinionRequest request = requestOpt.get();
        request.setStatus(status);
        
        if (status.equals("COMPLETED") && request.getCompletionDate() == null) {
            request.setCompletionDate(LocalDateTime.now());
        }
        
        return repository.save(request);
    }

    @Transactional
    public void deleteRequest(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Second opinion request not found with ID: " + id);
        }
        repository.deleteById(id);
    }
    
    @Transactional
    public SecondOpinionRequest createSecondOpinionRequest(
            Long patientId, 
            Long requestingDoctorId, 
            Long consultingDoctorId, 
            String conditionDescription, 
            String currentTreatment,
            boolean isUrgent) {
        
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        Optional<Doctor> requestingDoctorOpt = doctorRepository.findById(requestingDoctorId);
        Optional<Doctor> consultingDoctorOpt = doctorRepository.findById(consultingDoctorId);
        
        if (!patientOpt.isPresent()) {
            throw new IllegalArgumentException("Patient not found with ID: " + patientId);
        }
        
        if (!requestingDoctorOpt.isPresent()) {
            throw new IllegalArgumentException("Requesting doctor not found with ID: " + requestingDoctorId);
        }
        
        if (!consultingDoctorOpt.isPresent()) {
            throw new IllegalArgumentException("Consulting doctor not found with ID: " + consultingDoctorId);
        }
        
        SecondOpinionRequest request = new SecondOpinionRequest();
        request.setPatient(patientOpt.get());
        request.setRequestingDoctor(requestingDoctorOpt.get());
        request.setConsultingDoctor(consultingDoctorOpt.get());
        request.setConditionDescription(conditionDescription);
        request.setCurrentTreatment(currentTreatment);
        request.setRequestDate(LocalDateTime.now());
        request.setStatus("PENDING");
        request.setUrgent(isUrgent);
        
        return repository.save(request);
    }
    
    public List<SecondOpinionRequest> getPendingSecondOpinionRequestsByPatient(Long patientId) {
        return repository.findByPatientIdAndStatus(patientId, "PENDING");
    }
    
    @Transactional
    public SecondOpinionRequest linkToClinicalRecord(Long requestId, Long clinicalRecordId) {
        SecondOpinionRequest request = repository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Second opinion request not found with ID: " + requestId));
        
        ClinicalRecord clinicalRecord = clinicalRecordRepository.findById(clinicalRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Clinical record not found with ID: " + clinicalRecordId));
        
        request.setClinicalRecord(clinicalRecord);
        return repository.save(request);
    }
}
