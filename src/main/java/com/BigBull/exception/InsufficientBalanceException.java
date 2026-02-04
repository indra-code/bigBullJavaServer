package com.BigBull.exception;

/**
 * Exception thrown when wallet has insufficient balance for a transaction.
 */
public class InsufficientBalanceException extends RuntimeException {
    
    private Double requiredAmount;
    private Double availableBalance;
    
    public InsufficientBalanceException(String message) {
        super(message);
    }
    
    public InsufficientBalanceException(String message, Double requiredAmount, Double availableBalance) {
        super(message);
        this.requiredAmount = requiredAmount;
        this.availableBalance = availableBalance;
    }
    
    public InsufficientBalanceException(String message, Double requiredAmount, Double availableBalance, Throwable cause) {
        super(message, cause);
        this.requiredAmount = requiredAmount;
        this.availableBalance = availableBalance;
    }
    
    public Double getRequiredAmount() {
        return requiredAmount;
    }
    
    public Double getAvailableBalance() {
        return availableBalance;
    }
}
