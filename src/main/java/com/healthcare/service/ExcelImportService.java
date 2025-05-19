package com.healthcare.service;

import com.healthcare.model.Doctor;
import com.healthcare.model.User;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.UserRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
public class ExcelImportService {

    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private HospitalService hospitalService;
    
    private static final String DEFAULT_PASSWORD = "123456";
    
    @Transactional
    public List<Doctor> importDoctorsFromExcel(MultipartFile file, Long hospitalId) throws IOException {
        List<Doctor> importedDoctors = new ArrayList<>();
        
        try (InputStream is = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            
            // Skip header row
            if (rows.hasNext()) {
                rows.next();
            }
            
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                
                // Skip empty rows
                if (isRowEmpty(currentRow)) {
                    continue;
                }
                
                Doctor doctor = new Doctor();
                
                // Set doctor properties from Excel columns
                doctor.setName(currentRow.getCell(0).getStringCellValue());
                doctor.setSpecialty(currentRow.getCell(1).getStringCellValue());
                if (currentRow.getCell(2) != null) {
                    String additionalSpecialties = currentRow.getCell(2).getStringCellValue();
                    if (additionalSpecialties != null && !additionalSpecialties.isEmpty()) {
                        doctor.setAdditionalSpecialties(List.of(additionalSpecialties.split("\\s*,\\s*")));
                    }
                }
                doctor.setEmail(getCellStringValue(currentRow.getCell(3)));
                doctor.setPhoneNumber(getCellStringValue(currentRow.getCell(4)));
                
                // Generate unique doctor code
                String doctorCode = "DOC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                doctor.setDoctorCode(doctorCode);
                
                // Create user account for doctor
                User user = new User();
                user.setUsername(doctor.getEmail());
                user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
                user.setRole("DOCTOR");
                user.setFirstLogin(true);
                
                userRepository.save(user);
                doctor.setUser(user);
                
                // Set hospital
                doctor.setHospital(hospitalService.getHospitalById(hospitalId).orElseThrow(
                    () -> new IllegalArgumentException("Hospital not found with ID: " + hospitalId)
                ));
                
                doctor.setRegistrationDate(LocalDate.now());
                
                doctorRepository.save(doctor);
                importedDoctors.add(doctor);
            }
            
            workbook.close();
        }
        
        return importedDoctors;
    }
    
    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        if (row.getCell(0) == null) {
            return true;
        }
        return row.getCell(0).getStringCellValue().trim().isEmpty();
    }
    
    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        return cell.getStringCellValue().trim();
    }
}
