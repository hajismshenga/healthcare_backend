package com.healthcare.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
    
    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }
    
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        List<String> errors = new ArrayList<>();
        
        if (password == null || password.length() < 6) {
            errors.add("Password must be at least 6 characters long");
        } else {
            if (!password.matches(".*[0-9].*")) {
                errors.add("Password must contain at least one number");
            }
            if (!password.matches(".*[a-z].*")) {
                errors.add("Password must contain at least one lowercase letter");
            }
            if (!password.matches(".*[A-Z].*")) {
                errors.add("Password must contain at least one uppercase letter");
            }
        }
        
        if (!errors.isEmpty()) {
            context.disableDefaultConstraintViolation();
            String message = String.join(", ", errors);
            context.buildConstraintViolationWithTemplate(message)
                   .addConstraintViolation();
            return false;
        }
        
        return true;
    }
}
