package com.healthcare.repository;

import com.healthcare.model.PatientInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientInvitationRepository extends JpaRepository<PatientInvitation, Long> {
    
    Optional<PatientInvitation> findByInvitationId(String invitationId);
    
    boolean existsByInvitationId(String invitationId);
    
    List<PatientInvitation> findByPatientPatientId(String patientId);
    
    List<PatientInvitation> findByDoctorDoctorId(String doctorId);
    
    List<PatientInvitation> findByDoctorDoctorIdAndStatus(String doctorId, PatientInvitation.InvitationStatus status);
    
    List<PatientInvitation> findByPatientPatientIdAndStatus(String patientId, PatientInvitation.InvitationStatus status);
    
    List<PatientInvitation> findByStatusAndExpiryDateBefore(PatientInvitation.InvitationStatus status, LocalDateTime expiryDate);
    
    @Query("SELECT MAX(p.invitationId) FROM PatientInvitation p WHERE p.invitationId LIKE %?1%")
    String findMaxInvitationId(String prefix);
}
