package com.healthcare.repository;

import com.healthcare.model.Hospital;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    Optional<Hospital> findByHospitalId(String hospitalId);
    List<Hospital> findByUserUsername(String username);
}
