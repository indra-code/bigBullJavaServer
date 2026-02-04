package com.BigBull.exception;

/**
 * Exception thrown when an invalid transaction type is provided.
 */
public class InvalidTransactionTypeException extends RuntimeException {
    
    private String invalidType;
    
    public InvalidTransactionTypeException(String message) {
        super(message);
    }
    
    public InvalidTransactionTypeException(String message, String invalidType) {
        super(message);
        this.invalidType = invalidType;
    }
    
    public InvalidTransactionTypeException(String message, String invalidType, Throwable cause) {
        super(message, cause);
        this.invalidType = invalidType;
    }
    
    public String getInvalidType() {
        return invalidType;
    }
}
