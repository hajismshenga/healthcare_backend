package com.healthcare.util;

import com.healthcare.repository.LabTestRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TestIdGenerator {
    
    private static final String TEST_PREFIX = "TEST";
    private static final String SEPARATOR = "/";
    
    private final LabTestRepository labTestRepository;

    public TestIdGenerator(LabTestRepository labTestRepository) {
        this.labTestRepository = labTestRepository;
    }

    /**
     * Generates a new test ID based on current date and sequence number.
     * @return A new test ID in the format "TEST/YYYYMMDD/001"
     */
    public String generateTestId() {
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String datePrefix = TEST_PREFIX + SEPARATOR + currentDate + SEPARATOR;
        
        // Get the max test ID for today
        String maxId = labTestRepository.findMaxTestIdByDate(currentDate);
        
        // If no tests exist for today, start with 1
        if (maxId == null) {
            return datePrefix + "001";
        }
        
        // Extract the sequence number from the ID (e.g., "TEST/20250813/001" -> 1)
        int sequence = extractSequence(maxId);
        
        // Increment and return the new ID
        return datePrefix + String.format("%03d", sequence + 1);
    }
    
    private int extractSequence(String id) {
        try {
            // Extract the numeric part after the last separator
            String[] parts = id.split(SEPARATOR);
            if (parts.length > 2) {
                return Integer.parseInt(parts[2]);
            }
            // If format is unexpected, try to extract any numbers
            String numberStr = id.replaceAll("[^0-9]", "");
            if (!numberStr.isEmpty()) {
                return Integer.parseInt(numberStr);
            }
        } catch (NumberFormatException e) {
            // If parsing fails, start from 0
            return 0;
        }
        return 0;
    }
}
