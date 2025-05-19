package com.healthcare.controller;

import com.healthcare.model.Doctor;
import com.healthcare.service.ExcelImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/excel-import")
public class ExcelImportController {

    @Autowired
    private ExcelImportService excelImportService;

    @PostMapping("/doctors")
    public ResponseEntity<?> importDoctors(@RequestParam("file") MultipartFile file, 
                                          @RequestParam("hospitalId") Long hospitalId) {
        try {
            List<Doctor> importedDoctors = excelImportService.importDoctorsFromExcel(file, hospitalId);
            
            return ResponseEntity.ok(Map.of(
                "message", "Successfully imported " + importedDoctors.size() + " doctors",
                "importedCount", importedDoctors.size(),
                "doctors", importedDoctors
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
}
