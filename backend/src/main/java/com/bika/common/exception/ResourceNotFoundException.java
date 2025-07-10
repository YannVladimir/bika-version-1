package com.bika.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BikaException {
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
} 