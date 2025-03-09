package com.healthcare.controller;

import com.healthcare.model.Hospital;
import com.healthcare.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/hospitals")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @GetMapping
    public List<Hospital> getAllHospitals() {
        return hospitalService.getAllHospitals();
    }

    @GetMapping("/{id}")
    public Optional<Hospital> getHospitalById(@PathVariable Long id) {
        return hospitalService.getHospitalById(id);
    }

    @PostMapping
    public Hospital addHospital(@RequestBody Hospital hospital) {
        return hospitalService.addHospital(hospital);
    }

    @DeleteMapping("/{id}")
    public void deleteHospital(@PathVariable Long id) {
        hospitalService.deleteHospital(id);
    }
}
