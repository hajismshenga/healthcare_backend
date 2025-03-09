package com.healthcare.controller;

import com.healthcare.model.LaboratoryTest;
import com.healthcare.service.LaboratoryTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/laboratory-tests")
public class LaboratoryTestController {

    @Autowired
    private LaboratoryTestService laboratoryTestService;

    // Create laboratory test linked to an appointment
    @PostMapping("/{appointmentId}")
    public LaboratoryTest createLaboratoryTest(@PathVariable Long appointmentId,
                                               @RequestBody LaboratoryTest laboratoryTest) {
        return laboratoryTestService.createLaboratoryTest(appointmentId, laboratoryTest);
    }
}
