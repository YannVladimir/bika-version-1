package com.bika.common.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends BikaException {
    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
} 