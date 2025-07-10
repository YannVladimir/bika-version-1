package com.bika.common.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BikaException {
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
} 