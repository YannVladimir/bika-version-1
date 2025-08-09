package com.bika.document.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhysicalStorageValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhysicalStorage {
    String message() default "Invalid physical storage data";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 