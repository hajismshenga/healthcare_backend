package com.healthcare.service;

import com.healthcare.model.Patient;
import com.healthcare.model.SecondOpinion;
import com.healthcare.model.Doctor;
import com.healthcare.repository.PatientRepository;
import com.healthcare.repository.SecondOpinionRepository;
import com.healthcare.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class SecondOpinionService {

    @Autowired
    private SecondOpinionRepository secondOpinionRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;

    public SecondOpinion requestSecondOpinion(
        Long patientId,
        Long requestingDoctorId,
        String caseSummary,
        String currentTreatment,
        String additionalInformation
    ) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        Doctor requestingDoctor = doctorRepository.findById(requestingDoctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        SecondOpinion opinion = SecondOpinion.builder()
            .patient(patient)
            .requestingDoctor(requestingDoctor)
            .requestDate(LocalDate.now())
            .caseSummary(caseSummary)
            .currentTreatment(currentTreatment)
            .additionalInformation(additionalInformation)
            .status(SecondOpinion.SecondOpinionStatus.PENDING)
            .build();
            
        return secondOpinionRepository.save(opinion);
    }

    public List<SecondOpinion> getPendingOpinions(Long doctorId) {
        return secondOpinionRepository.findByReviewingDoctorIdAndStatus(
            doctorId,
            SecondOpinion.SecondOpinionStatus.PENDING
        );
    }

    public SecondOpinion reviewSecondOpinion(
        Long opinionId,
        Long reviewingDoctorId,
        String notes,
        String medications,
        String tests,
        String recommendations
    ) {
        SecondOpinion opinion = secondOpinionRepository.findById(opinionId)
            .orElseThrow(() -> new RuntimeException("Second opinion request not found"));
            
        Doctor reviewingDoctor = doctorRepository.findById(reviewingDoctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found"));
            
        opinion.setReviewingDoctor(reviewingDoctor);
        opinion.setReviewingDoctorNotes(notes);
        opinion.setRecommendedMedications(medications);
        opinion.setRecommendedTests(tests);
        opinion.setAdditionalRecommendations(recommendations);
        opinion.setStatus(SecondOpinion.SecondOpinionStatus.COMPLETED);
        
        return secondOpinionRepository.save(opinion);
    }
}
