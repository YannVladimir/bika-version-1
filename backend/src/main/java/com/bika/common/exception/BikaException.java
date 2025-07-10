package com.bika.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BikaException extends RuntimeException {
    private final HttpStatus status;
    private final String message;

    public BikaException(String message) {
        this(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public BikaException(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }
} 