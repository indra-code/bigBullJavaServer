package com.BigBull.exception;

/**
 * Exception thrown when an order is invalid.
 */
public class InvalidOrderException extends RuntimeException {
    
    private String validationFailureReason;
    
    public InvalidOrderException(String message) {
        super(message);
    }
    
    public InvalidOrderException(String message, String validationFailureReason) {
        super(message);
        this.validationFailureReason = validationFailureReason;
    }
    
    public InvalidOrderException(String message, String validationFailureReason, Throwable cause) {
        super(message, cause);
        this.validationFailureReason = validationFailureReason;
    }
    
    public String getValidationFailureReason() {
        return validationFailureReason;
    }
}
