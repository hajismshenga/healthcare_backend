package com.healthcare.repository;

import com.healthcare.model.SecondOpinionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SecondOpinionRequestRepository extends JpaRepository<SecondOpinionRequest, Long> {
    List<SecondOpinionRequest> findByConsultingDoctorId(Long doctorId);
    Page<SecondOpinionRequest> findByConsultingDoctorId(Long doctorId, Pageable pageable);
    
    List<SecondOpinionRequest> findByRequestingDoctorId(Long doctorId);
    Page<SecondOpinionRequest> findByRequestingDoctorId(Long doctorId, Pageable pageable);
    
    List<SecondOpinionRequest> findByPatientId(Long patientId);
    Page<SecondOpinionRequest> findByPatientId(Long patientId, Pageable pageable);
    
    List<SecondOpinionRequest> findByConsultingDoctorIdAndStatus(Long doctorId, String status);
    List<SecondOpinionRequest> findByRequestingDoctorIdAndStatus(Long doctorId, String status);
    List<SecondOpinionRequest> findByPatientIdAndStatus(Long patientId, String status);
    
    @Query("SELECT sor FROM SecondOpinionRequest sor WHERE sor.requestDate BETWEEN :startDate AND :endDate")
    List<SecondOpinionRequest> findByRequestDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT sor FROM SecondOpinionRequest sor WHERE sor.conditionDescription LIKE %:keyword% OR sor.currentTreatment LIKE %:keyword%")
    List<SecondOpinionRequest> searchByKeyword(String keyword);
}
