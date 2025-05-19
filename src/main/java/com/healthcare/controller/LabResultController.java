package com.healthcare.controller;

import com.healthcare.model.LabResult;
import com.healthcare.service.LabResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lab-results")
public class LabResultController {

    @Autowired
    private LabResultService service;

    @PostMapping
    public LabResult addResult(@RequestBody LabResult result) {
        return service.addResult(result);
    }

    @GetMapping
    public List<LabResult> getAllResults() {
        return service.getAllResults();
    }

    @GetMapping("/{id}")
    public Optional<LabResult> getResult(@PathVariable Long id) {
        return service.getResultById(id);
    }
}
