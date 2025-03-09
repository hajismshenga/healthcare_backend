package com.healthcare.repository;

import com.healthcare.model.DoctorEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorEvaluationRepository extends JpaRepository<DoctorEvaluation, Long> {
}
