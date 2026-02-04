package com.BigBull.exception;

/**
 * Exception thrown when market data is unavailable.
 */
public class MarketDataUnavailableException extends RuntimeException {
    
    private String symbol;
    
    public MarketDataUnavailableException(String message) {
        super(message);
    }
    
    public MarketDataUnavailableException(String message, String symbol) {
        super(message);
        this.symbol = symbol;
    }
    
    public MarketDataUnavailableException(String message, String symbol, Throwable cause) {
        super(message, cause);
        this.symbol = symbol;
    }
    
    public String getSymbol() {
        return symbol;
    }
}
