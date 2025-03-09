package com.healthcare.controller;

import com.healthcare.model.LaboratoryTest;
import com.healthcare.service.LaboratoryTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/laboratory-tests")
public class LaboratoryTestController {

    @Autowired
    private LaboratoryTestService laboratoryTestService;

    // Get all laboratory tests
    @GetMapping
    public List<LaboratoryTest> getAllLaboratoryTests() {
        return laboratoryTestService.getAllLaboratoryTests();
    }

    // Get laboratory test by ID
    @GetMapping("/{id}")
    public Optional<LaboratoryTest> getLaboratoryTestById(@PathVariable Long id) {
        return laboratoryTestService.getLaboratoryTestById(id);
    }

    // Create a new laboratory test
    @PostMapping("/{doctorId}/{patientId}/{laboratoryId}")
    public LaboratoryTest createLaboratoryTest(@PathVariable Long doctorId,
                                               @PathVariable Long patientId,
                                               @PathVariable Long laboratoryId,
                                               @RequestBody LaboratoryTest laboratoryTest) {
        try {
            return laboratoryTestService.createLaboratoryTest(doctorId, patientId, laboratoryId, laboratoryTest);
        } catch (RuntimeException e) {
            // Log the error for debugging
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating laboratory test: " + e.getMessage(), e);
        }
    }
}
