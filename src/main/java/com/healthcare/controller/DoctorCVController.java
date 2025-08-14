package com.healthcare.controller;

import com.healthcare.dto.DoctorCVResponse;
import com.healthcare.service.DoctorCVService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/doctor-cv")
public class DoctorCVController {
    
    private final DoctorCVService doctorCVService;

    public DoctorCVController(DoctorCVService doctorCVService) {
        this.doctorCVService = doctorCVService;
        log.info("DoctorCVController initialized!");
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("DoctorCVController is working!");
    }

    @GetMapping("/all")
    public ResponseEntity<List<DoctorCVResponse>> getAllDoctorsCV() {
        log.info("Fetching CV information for all doctors");
        List<DoctorCVResponse> doctorsCV = doctorCVService.getAllDoctorsCV();
        return ResponseEntity.ok(doctorsCV);
    }

    @GetMapping("/{doctorId}")
    public ResponseEntity<DoctorCVResponse> getDoctorCV(@PathVariable String doctorId) {
        log.info("Fetching CV information for doctor: {}", doctorId);
        DoctorCVResponse doctorCV = doctorCVService.getDoctorCV(doctorId);
        return ResponseEntity.ok(doctorCV);
    }

    @GetMapping("/profession/{profession}")
    public ResponseEntity<List<DoctorCVResponse>> getDoctorsByProfession(@PathVariable String profession) {
        log.info("Fetching CV information for doctors with profession: {}", profession);
        List<DoctorCVResponse> doctorsCV = doctorCVService.getDoctorsByProfession(profession);
        return ResponseEntity.ok(doctorsCV);
    }

    @GetMapping("/available")
    public ResponseEntity<List<DoctorCVResponse>> getAvailableDoctors() {
        log.info("Fetching CV information for available doctors");
        List<DoctorCVResponse> doctorsCV = doctorCVService.getAvailableDoctors();
        return ResponseEntity.ok(doctorsCV);
    }
}
