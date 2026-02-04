package com.BigBull.exception;

/**
 * Exception thrown when an invalid asset quantity is provided.
 */
public class InvalidAssetQuantityException extends RuntimeException {
    
    private Double invalidQuantity;
    
    public InvalidAssetQuantityException(String message) {
        super(message);
    }
    
    public InvalidAssetQuantityException(String message, Double invalidQuantity) {
        super(message);
        this.invalidQuantity = invalidQuantity;
    }
    
    public InvalidAssetQuantityException(String message, Double invalidQuantity, Throwable cause) {
        super(message, cause);
        this.invalidQuantity = invalidQuantity;
    }
    
    public Double getInvalidQuantity() {
        return invalidQuantity;
    }
}
