package com.healthcare.exception;

public class PrescriptionNotFoundException extends RuntimeException {
    
    public PrescriptionNotFoundException(String message) {
        super(message);
    }
    
    public PrescriptionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
