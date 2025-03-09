package com.healthcare.repository;

import com.healthcare.model.Laboratory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LaboratoryRepository extends JpaRepository<Laboratory, Long> {
    List<Laboratory> findByHospitalId(Long hospitalId);
}
