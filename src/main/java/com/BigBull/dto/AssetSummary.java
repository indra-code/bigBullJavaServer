package com.BigBull.dto;

public class AssetSummary {
    private Long id;
    private String symbol;
    private String name;
    private String type;
    private double quantity;
    private double costPerUnit;
    private double currentPrice;
    private double totalValue;
    private double totalCostValue;
    private double unrealizedGain;
    private double gainPercentage;

    public AssetSummary() {
    }

    public AssetSummary(Long id, String symbol, String name, String type, double quantity,
                        double costPerUnit, double currentPrice, double totalValue,
                        double totalCostValue, double unrealizedGain, double gainPercentage) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.type = type;
        this.quantity = quantity;
        this.costPerUnit = costPerUnit;
        this.currentPrice = currentPrice;
        this.totalValue = totalValue;
        this.totalCostValue = totalCostValue;
        this.unrealizedGain = unrealizedGain;
        this.gainPercentage = gainPercentage;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public double getCostPerUnit() { return costPerUnit; }
    public void setCostPerUnit(double costPerUnit) { this.costPerUnit = costPerUnit; }

    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }

    public double getTotalValue() { return totalValue; }
    public void setTotalValue(double totalValue) { this.totalValue = totalValue; }

    public double getTotalCostValue() { return totalCostValue; }
    public void setTotalCostValue(double totalCostValue) { this.totalCostValue = totalCostValue; }

    public double getUnrealizedGain() { return unrealizedGain; }
    public void setUnrealizedGain(double unrealizedGain) { this.unrealizedGain = unrealizedGain; }

    public double getGainPercentage() { return gainPercentage; }
    public void setGainPercentage(double gainPercentage) { this.gainPercentage = gainPercentage; }
}
