package com.BigBull.exception;

/**
 * Exception thrown when portfolio is not found.
 */
public class PortfolioNotFoundException extends RuntimeException {
    
    private Long portfolioId;
    
    public PortfolioNotFoundException(String message) {
        super(message);
    }
    
    public PortfolioNotFoundException(String message, Long portfolioId) {
        super(message);
        this.portfolioId = portfolioId;
    }
    
    public PortfolioNotFoundException(String message, Long portfolioId, Throwable cause) {
        super(message, cause);
        this.portfolioId = portfolioId;
    }
    
    public Long getPortfolioId() {
        return portfolioId;
    }
}
