package com.healthcare.service;

import com.healthcare.dto.DoctorMedicationComment;
import com.healthcare.exception.DoctorNotFoundException;
import com.healthcare.exception.PatientNotFoundException;
import com.healthcare.exception.PrescriptionNotFoundException;
import com.healthcare.model.Doctor;
import com.healthcare.model.Patient;
import com.healthcare.model.Prescription;
import com.healthcare.repository.DoctorMedicationCommentRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.PatientRepository;
import com.healthcare.repository.PrescriptionRepository;
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
public class DoctorMedicationCommentService {

    private final DoctorMedicationCommentRepository doctorMedicationCommentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PrescriptionRepository prescriptionRepository;

    @Autowired
    public DoctorMedicationCommentService(DoctorMedicationCommentRepository doctorMedicationCommentRepository,
                                        DoctorRepository doctorRepository,
                                        PatientRepository patientRepository,
                                        PrescriptionRepository prescriptionRepository) {
        this.doctorMedicationCommentRepository = doctorMedicationCommentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    public String addComment(DoctorMedicationComment commentRequest) {
        log.info("Adding medication comment from doctor: {} for patient: {} on prescription: {}", 
                commentRequest.getDoctorId(), commentRequest.getPatientId(), commentRequest.getPrescriptionId());
        
        // Verify doctor exists
        Doctor doctor = doctorRepository.findByDoctorId(commentRequest.getDoctorId())
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with ID: " + commentRequest.getDoctorId()));
        
        // Verify patient exists
        Patient patient = patientRepository.findByPatientId(commentRequest.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + commentRequest.getPatientId()));
        
        // Verify prescription exists
        Prescription prescription = prescriptionRepository.findByPrescriptionId(commentRequest.getPrescriptionId())
                .orElseThrow(() -> new PrescriptionNotFoundException("Prescription not found with ID: " + commentRequest.getPrescriptionId()));
        
        // Generate comment ID
        String commentId = generateCommentId();
        
        // Create comment entity
        com.healthcare.model.DoctorMedicationComment comment = com.healthcare.model.DoctorMedicationComment.builder()
                .commentId(commentId)
                .prescription(prescription)
                .doctor(doctor)
                .patient(patient)
                .comment(commentRequest.getComment())
                .commentType(commentRequest.getCommentType())
                .recommendations(commentRequest.getRecommendations())
                .sideEffects(commentRequest.getSideEffects())
                .dosageAdjustments(commentRequest.getDosageAdjustments())
                .alternativeMedications(commentRequest.getAlternativeMedications())
                .build();
        
        com.healthcare.model.DoctorMedicationComment savedComment = doctorMedicationCommentRepository.save(comment);
        log.info("Successfully added medication comment with ID: {}", commentId);
        
        return commentId;
    }

    public List<DoctorMedicationComment> getPatientComments(String patientId) {
        log.info("Fetching medication comments for patient: {}", patientId);
        
        List<com.healthcare.model.DoctorMedicationComment> comments = 
                doctorMedicationCommentRepository.findByPatientPatientId(patientId);
        
        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DoctorMedicationComment> getDoctorComments(String doctorId) {
        log.info("Fetching medication comments by doctor: {}", doctorId);
        
        List<com.healthcare.model.DoctorMedicationComment> comments = 
                doctorMedicationCommentRepository.findByDoctorDoctorId(doctorId);
        
        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DoctorMedicationComment> getPrescriptionComments(String prescriptionId) {
        log.info("Fetching medication comments for prescription: {}", prescriptionId);
        
        List<com.healthcare.model.DoctorMedicationComment> comments = 
                doctorMedicationCommentRepository.findByPrescriptionPrescriptionId(prescriptionId);
        
        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void updateComment(String commentId, DoctorMedicationComment updatedComment) {
        log.info("Updating medication comment: {}", commentId);
        
        com.healthcare.model.DoctorMedicationComment existingComment = 
                doctorMedicationCommentRepository.findByCommentId(commentId)
                        .orElseThrow(() -> new IllegalArgumentException("Comment not found with ID: " + commentId));
        
        existingComment.setComment(updatedComment.getComment());
        existingComment.setCommentType(updatedComment.getCommentType());
        existingComment.setRecommendations(updatedComment.getRecommendations());
        existingComment.setSideEffects(updatedComment.getSideEffects());
        existingComment.setDosageAdjustments(updatedComment.getDosageAdjustments());
        existingComment.setAlternativeMedications(updatedComment.getAlternativeMedications());
        
        doctorMedicationCommentRepository.save(existingComment);
        log.info("Successfully updated medication comment: {}", commentId);
    }

    public void deleteComment(String commentId) {
        log.info("Deleting medication comment: {}", commentId);
        
        if (!doctorMedicationCommentRepository.existsByCommentId(commentId)) {
            throw new IllegalArgumentException("Comment not found with ID: " + commentId);
        }
        
        doctorMedicationCommentRepository.deleteByCommentId(commentId);
        log.info("Successfully deleted medication comment: {}", commentId);
    }

    private String generateCommentId() {
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "COMM/" + today + "/";
        
        // For simplicity, using timestamp-based ID generation
        return prefix + System.currentTimeMillis() % 1000;
    }

    private DoctorMedicationComment convertToDto(com.healthcare.model.DoctorMedicationComment comment) {
        return DoctorMedicationComment.builder()
                .commentId(comment.getCommentId())
                .prescriptionId(comment.getPrescription().getPrescriptionId())
                .doctorId(comment.getDoctor().getDoctorId())
                .patientId(comment.getPatient().getPatientId())
                .comment(comment.getComment())
                .commentType(comment.getCommentType())
                .recommendations(comment.getRecommendations())
                .sideEffects(comment.getSideEffects())
                .dosageAdjustments(comment.getDosageAdjustments())
                .alternativeMedications(comment.getAlternativeMedications())
                .commentDate(comment.getCommentDate())
                .lastUpdated(comment.getLastUpdated())
                .build();
    }
}
