package com.healthcare.repository;

import com.healthcare.model.SecondOpinion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SecondOpinionRepository extends JpaRepository<SecondOpinion, Long> {
    List<SecondOpinion> findByReviewingDoctorIdAndStatus(
        Long doctorId,
        SecondOpinion.SecondOpinionStatus status
    );
}
