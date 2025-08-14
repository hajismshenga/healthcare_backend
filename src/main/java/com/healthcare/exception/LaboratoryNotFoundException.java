package com.healthcare.exception;

public class LaboratoryNotFoundException extends RuntimeException {
    public LaboratoryNotFoundException(String message) {
        super(message);
    }

    public LaboratoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
