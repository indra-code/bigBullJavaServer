package com.BigBull.dto;

import com.BigBull.entity.Asset;
import com.BigBull.entity.Wallet;

public class TransactionResponse {
    private Long transactionId;
    private String username;
    private Asset asset;
    private String type;
    private int units;
    private double price;
    private double totalAmount;
    private String timestamp;
    private double walletBalance;
    private double assetQuantity;
    private String message;

    public TransactionResponse() {
    }

    public TransactionResponse(Long transactionId, String username, Asset asset, String type,
                             int units, double price, double totalAmount, String timestamp,
                             double walletBalance, double assetQuantity, String message) {
        this.transactionId = transactionId;
        this.username = username;
        this.asset = asset;
        this.type = type;
        this.units = units;
        this.price = price;
        this.totalAmount = totalAmount;
        this.timestamp = timestamp;
        this.walletBalance = walletBalance;
        this.assetQuantity = assetQuantity;
        this.message = message;
    }

    // Getters & Setters
    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Asset getAsset() { return asset; }
    public void setAsset(Asset asset) { this.asset = asset; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getUnits() { return units; }
    public void setUnits(int units) { this.units = units; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public double getWalletBalance() { return walletBalance; }
    public void setWalletBalance(double walletBalance) { this.walletBalance = walletBalance; }

    public double getAssetQuantity() { return assetQuantity; }
    public void setAssetQuantity(double assetQuantity) { this.assetQuantity = assetQuantity; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}