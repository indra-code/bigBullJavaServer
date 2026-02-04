package com.BigBull.exception;

/**
 * Exception thrown when wallet is not found.
 */
public class WalletNotFoundException extends RuntimeException {
    
    private Long walletId;
    
    public WalletNotFoundException(String message) {
        super(message);
    }
    
    public WalletNotFoundException(String message, Long walletId) {
        super(message);
        this.walletId = walletId;
    }
    
    public WalletNotFoundException(String message, Long walletId, Throwable cause) {
        super(message, cause);
        this.walletId = walletId;
    }
    
    public Long getWalletId() {
        return walletId;
    }
}
