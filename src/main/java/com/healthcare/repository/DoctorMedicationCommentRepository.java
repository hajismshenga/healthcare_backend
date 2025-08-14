package com.healthcare.repository;

import com.healthcare.model.DoctorMedicationComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorMedicationCommentRepository extends JpaRepository<DoctorMedicationComment, Long> {
    
    Optional<DoctorMedicationComment> findByCommentId(String commentId);
    
    boolean existsByCommentId(String commentId);
    
    List<DoctorMedicationComment> findByPatientPatientId(String patientId);
    
    List<DoctorMedicationComment> findByDoctorDoctorId(String doctorId);
    
    List<DoctorMedicationComment> findByPrescriptionPrescriptionId(String prescriptionId);
    
    List<DoctorMedicationComment> findByPatientPatientIdAndDoctorDoctorId(String patientId, String doctorId);
    
    List<DoctorMedicationComment> findByCommentType(DoctorMedicationComment.CommentType commentType);
    
    void deleteByCommentId(String commentId);
}
