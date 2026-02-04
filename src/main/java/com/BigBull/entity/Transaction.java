package com.BigBull.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "transactions")
public class Transaction {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    @JsonBackReference
    private Asset asset;

    @Column(nullable = false)
    private Double quantity; // Database column name

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    @Column(nullable = false)
    private String type; // BUY or SELL

    @Column(nullable = false)
    private Integer units;

    @Column(nullable = false)
    private Double price; // Current market price at transaction time

    @Column(name = "price_per_unit", nullable = false)
    private Double pricePerUnit; // Same as price, kept for compatibility

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @PrePersist
    protected void onCreate() {
        transactionDate = LocalDateTime.now();
        // Ensure both price fields are set
        if (price == null && pricePerUnit != null) {
            price = pricePerUnit;
        } else if (pricePerUnit == null && price != null) {
            pricePerUnit = price;
        }
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUnits(Integer units) {
        this.units = units;
    }

    public void setPrice(Double price) {
        this.price = price;
        // Keep both fields in sync
        if (this.pricePerUnit == null) {
            this.pricePerUnit = price;
        }
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
        // Keep both fields in sync
        if (this.price == null) {
            this.price = pricePerUnit;
        }
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}
