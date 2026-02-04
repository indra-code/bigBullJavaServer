package com.BigBull.exception;

/**
 * Exception thrown when an invalid amount is provided for an operation.
 */
public class InvalidAmountException extends RuntimeException {
    
    private String operationType;
    private Double amount;
    
    public InvalidAmountException(String message) {
        super(message);
    }
    
    public InvalidAmountException(String message, String operationType, Double amount) {
        super(message);
        this.operationType = operationType;
        this.amount = amount;
    }
    
    public InvalidAmountException(String message, String operationType, Double amount, Throwable cause) {
        super(message, cause);
        this.operationType = operationType;
        this.amount = amount;
    }
    
    public String getOperationType() {
        return operationType;
    }
    
    public Double getAmount() {
        return amount;
    }
}
