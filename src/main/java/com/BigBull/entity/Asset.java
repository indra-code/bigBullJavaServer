package com.BigBull.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "assets")
public class Asset {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String type;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String symbol;
    
    @Column(nullable = false)
    private Double quantity;
    
    @Column(name = "cost_per_unit")
    private Double costPerUnit;
    
    // Constructors
    public Asset() {
        this.quantity = 0.0;
        this.costPerUnit = 0.0;
    }
    
    public Asset(String type, String name, String symbol) {
        this.type = type;
        this.name = name;
        this.symbol = symbol;
        this.quantity = 0.0;
        this.costPerUnit = 0.0;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public Double getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
    
    public Double getCostPerUnit() {
        return costPerUnit;
    }
    
    public void setCostPerUnit(Double costPerUnit) {
        this.costPerUnit = costPerUnit;
    }
    
    // Business methods
    public void addQuantity(Double amount) {
        this.quantity += amount;
    }
    
    public void removeQuantity(Double amount) {
        this.quantity -= amount;
        if (this.quantity < 0) {
            this.quantity = 0.0;
        }
    }
    
    // Method to update cost per unit when buying more shares (weighted average)
    public void updateCostPerUnit(Double newQuantity, Double newPrice) {
        if (this.quantity == 0.0 || this.costPerUnit == null) {
            // First purchase or cost per unit not set
            this.costPerUnit = newPrice;
        } else {
            // Calculate weighted average: (old_qty * old_price + new_qty * new_price) / total_qty
            Double totalCost = (this.quantity * this.costPerUnit) + (newQuantity * newPrice);
            Double totalQuantity = this.quantity + newQuantity;
            this.costPerUnit = totalCost / totalQuantity;
        }
    }
}
