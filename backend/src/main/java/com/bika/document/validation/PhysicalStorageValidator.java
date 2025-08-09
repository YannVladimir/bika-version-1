package com.bika.document.validation;

import com.bika.document.dto.PhysicalStorageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PhysicalStorageValidator implements ConstraintValidator<ValidPhysicalStorage, String> {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Validator validator;

    @Override
    public void initialize(ValidPhysicalStorage constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        try {
            // Parse JSON to PhysicalStorageDTO
            PhysicalStorageDTO physicalStorage = objectMapper.readValue(value, PhysicalStorageDTO.class);
            
            // Validate the DTO using Bean Validation
            Set<ConstraintViolation<PhysicalStorageDTO>> violations = validator.validate(physicalStorage);
            
            if (!violations.isEmpty()) {
                // Add specific error messages
                context.disableDefaultConstraintViolation();
                for (ConstraintViolation<PhysicalStorageDTO> violation : violations) {
                    context.buildConstraintViolationWithTemplate(violation.getMessage())
                           .addConstraintViolation();
                }
                return false;
            }
            
            return true;
        } catch (Exception e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid JSON format for physical storage")
                   .addConstraintViolation();
            return false;
        }
    }
} 