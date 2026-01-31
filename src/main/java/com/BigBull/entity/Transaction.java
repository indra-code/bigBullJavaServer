package com.BigBull.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;
    
    @Column(nullable = false)
    private String type; // BUY or SELL
    
    @Column(nullable = false)
    private Double quantity;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(nullable = false)
    private LocalDateTime transactionDate;
    
    // Constructors
    public Transaction() {}
    
    public Transaction(Asset asset, String type, Double quantity, Double price, LocalDateTime transactionDate) {
        this.asset = asset;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.transactionDate = transactionDate;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Asset getAsset() {
        return asset;
    }
    
    public void setAsset(Asset asset) {
        this.asset = asset;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Double getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}
