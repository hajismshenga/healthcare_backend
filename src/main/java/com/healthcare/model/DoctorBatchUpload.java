package com.healthcare.model;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorBatchUpload {
    private List<Doctor> doctors;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Doctor {
        private String name;
        private String profession;
        private String specialization;
        private String qualifications;
        private String experienceYears;
        private String email;
        private String phoneNumber;
    }
}
