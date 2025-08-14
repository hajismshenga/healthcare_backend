package com.healthcare.util;

import com.healthcare.repository.HospitalRepository;
import org.springframework.stereotype.Component;

/**
 * Utility class for generating various IDs in the system.
 * Currently, most IDs are manually assigned, but this class is kept for future use.
 */
@Component
public class IdGenerator {
    
    private static final String HOSPITAL_PREFIX = "HOSP";
    private static final String SEPARATOR = "/";
    
    private final HospitalRepository hospitalRepository;

    public IdGenerator(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    /**
     * Generates a new hospital ID based on the latest one in the database.
     * @return A new hospital ID in the format "HOSP/001"
     */
    public String generateHospitalId() {
        try {
            // Get the max hospital ID from the database
            String maxId = hospitalRepository.findMaxHospitalId();
            
            // If no hospitals exist yet, start with 1
            if (maxId == null || maxId.trim().isEmpty()) {
                return formatId(HOSPITAL_PREFIX, 1);
            }
            
            // Extract the sequence number from the ID (e.g., "HOSP/001" -> 1)
            int sequence = extractSequence(maxId);
            
            // Increment and return the new ID
            return formatId(HOSPITAL_PREFIX, sequence + 1);
        } catch (Exception e) {
            // If any error occurs, start from 1
            return formatId(HOSPITAL_PREFIX, 1);
        }
    }
    
    private int extractSequence(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return 0;
            }
            
            // Extract the numeric part after the last separator
            String[] parts = id.split(SEPARATOR);
            if (parts.length > 1) {
                return Integer.parseInt(parts[1]);
            }
            // If format is unexpected, try to extract any numbers
            String numberStr = id.replaceAll("[^0-9]", "");
            if (!numberStr.isEmpty()) {
                return Integer.parseInt(numberStr);
            }
        } catch (NumberFormatException e) {
            // If parsing fails, start from 1
            return 0;
        }
        return 0;
    }
    
    private String formatId(String prefix, int sequence) {
        return String.format("%s%s%03d", prefix, SEPARATOR, sequence);
    }
}
