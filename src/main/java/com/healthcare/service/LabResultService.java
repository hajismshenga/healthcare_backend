package com.healthcare.service;

import com.healthcare.model.LabResult;
import com.healthcare.repository.LabResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LabResultService {

    @Autowired
    private LabResultRepository repository;

    public LabResult addResult(LabResult result) {
        result.setResultDate(LocalDateTime.now());
        return repository.save(result);
    }

    public List<LabResult> getAllResults() {
        return repository.findAll();
    }

    public Optional<LabResult> getResultById(Long id) {
        return repository.findById(id);
    }
}
