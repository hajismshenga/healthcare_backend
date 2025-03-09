package com.healthcare.repository;

import com.healthcare.model.LaboratoryTest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LaboratoryTestRepository extends JpaRepository<LaboratoryTest, Long> {
}
