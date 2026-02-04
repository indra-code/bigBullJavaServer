package com.BigBull.exception;

/**
 * Exception thrown when asset is not found.
 */
public class AssetNotFoundException extends RuntimeException {
    
    private Long assetId;
    private String symbol;
    
    public AssetNotFoundException(String message) {
        super(message);
    }
    
    public AssetNotFoundException(String message, Long assetId) {
        super(message);
        this.assetId = assetId;
    }
    
    public AssetNotFoundException(String message, Long assetId, String symbol) {
        super(message);
        this.assetId = assetId;
        this.symbol = symbol;
    }
    
    public AssetNotFoundException(String message, Long assetId, String symbol, Throwable cause) {
        super(message, cause);
        this.assetId = assetId;
        this.symbol = symbol;
    }
    
    public Long getAssetId() {
        return assetId;
    }
    
    public String getSymbol() {
        return symbol;
    }
}
