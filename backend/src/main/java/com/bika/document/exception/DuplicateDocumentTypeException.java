package com.bika.document.exception;

public class DuplicateDocumentTypeException extends RuntimeException {
    public DuplicateDocumentTypeException(String message) {
        super(message);
    }
    
    public DuplicateDocumentTypeException(String message, Throwable cause) {
        super(message, cause);
    }
} 