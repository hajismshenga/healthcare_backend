package com.healthcare.repository;

import com.healthcare.model.MedicalHistory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {

    List<MedicalHistory> findAllById(Long patientId);
}
