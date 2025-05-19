package com.healthcare.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class IdGenerator {
    
    private final AtomicInteger hospitalCounter = new AtomicInteger(0);
    private final AtomicInteger doctorCounter = new AtomicInteger(0);
    private final AtomicInteger labCounter = new AtomicInteger(0);
    
    private static final String HOSPITAL_PREFIX = "HOSP";
    private static final String DOCTOR_PREFIX = "DID";
    private static final String LAB_PREFIX = "LAB";
    private static final String SEPARATOR = "/"; // Can be changed to "." if needed
    
    public String generateHospitalId() {
        int sequence = hospitalCounter.incrementAndGet();
        return formatId(HOSPITAL_PREFIX, sequence);
    }
    
    public String generateDoctorId() {
        int sequence = doctorCounter.incrementAndGet();
        return formatId(DOCTOR_PREFIX, sequence);
    }
    
    public String generateLabId() {
        int sequence = labCounter.incrementAndGet();
        return formatId(LAB_PREFIX, sequence);
    }
    
    private String formatId(String prefix, int sequence) {
        return String.format("%s%s%03d", prefix, SEPARATOR, sequence);
    }
    
    
    public void setCounters(int hospitalCount, int doctorCount, int labCount) {
        hospitalCounter.set(hospitalCount);
        doctorCounter.set(doctorCount);
        labCounter.set(labCount);
    }
}
