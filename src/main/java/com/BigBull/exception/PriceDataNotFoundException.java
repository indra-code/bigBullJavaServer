package com.BigBull.exception;

/**
 * Exception thrown when price data for a symbol is not found.
 */
public class PriceDataNotFoundException extends RuntimeException {
    
    private String symbol;
    
    public PriceDataNotFoundException(String message) {
        super(message);
    }
    
    public PriceDataNotFoundException(String message, String symbol) {
        super(message);
        this.symbol = symbol;
    }
    
    public PriceDataNotFoundException(String message, String symbol, Throwable cause) {
        super(message, cause);
        this.symbol = symbol;
    }
    
    public String getSymbol() {
        return symbol;
    }
}
