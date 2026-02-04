package com.BigBull.exception;

/**
 * Exception thrown when a transaction fails to execute.
 */
public class TransactionFailedException extends RuntimeException {
    
    private String failureReason;
    
    public TransactionFailedException(String message) {
        super(message);
    }
    
    public TransactionFailedException(String message, String failureReason) {
        super(message);
        this.failureReason = failureReason;
    }
    
    public TransactionFailedException(String message, String failureReason, Throwable cause) {
        super(message, cause);
        this.failureReason = failureReason;
    }
    
    public String getFailureReason() {
        return failureReason;
    }
}
