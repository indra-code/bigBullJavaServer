package com.BigBull.exception;

/**
 * Exception thrown when transaction is not found.
 */
public class TransactionNotFoundException extends RuntimeException {
    
    private Long transactionId;
    
    public TransactionNotFoundException(String message) {
        super(message);
    }
    
    public TransactionNotFoundException(String message, Long transactionId) {
        super(message);
        this.transactionId = transactionId;
    }
    
    public TransactionNotFoundException(String message, Long transactionId, Throwable cause) {
        super(message, cause);
        this.transactionId = transactionId;
    }
    
    public Long getTransactionId() {
        return transactionId;
    }
}
