package com.BigBull.exception;

/**
 * Exception thrown when order execution fails.
 */
public class OrderExecutionException extends RuntimeException {
    
    private Long orderId;
    private String failureReason;
    
    public OrderExecutionException(String message) {
        super(message);
    }
    
    public OrderExecutionException(String message, Long orderId, String failureReason) {
        super(message);
        this.orderId = orderId;
        this.failureReason = failureReason;
    }
    
    public OrderExecutionException(String message, Long orderId, String failureReason, Throwable cause) {
        super(message, cause);
        this.orderId = orderId;
        this.failureReason = failureReason;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public String getFailureReason() {
        return failureReason;
    }
}
