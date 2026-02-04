package com.BigBull.exception;

/**
 * Exception thrown when asset has insufficient quantity for a transaction.
 */
public class InsufficientAssetException extends RuntimeException {
    
    private String symbol;
    private Double requiredQuantity;
    private Double availableQuantity;
    
    public InsufficientAssetException(String message) {
        super(message);
    }
    
    public InsufficientAssetException(String message, String symbol, Double requiredQuantity, Double availableQuantity) {
        super(message);
        this.symbol = symbol;
        this.requiredQuantity = requiredQuantity;
        this.availableQuantity = availableQuantity;
    }
    
    public InsufficientAssetException(String message, String symbol, Double requiredQuantity, Double availableQuantity, Throwable cause) {
        super(message, cause);
        this.symbol = symbol;
        this.requiredQuantity = requiredQuantity;
        this.availableQuantity = availableQuantity;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public Double getRequiredQuantity() {
        return requiredQuantity;
    }
    
    public Double getAvailableQuantity() {
        return availableQuantity;
    }
}
