package com.BigBull.dto;

import java.util.List;

public class PortfolioSummary {
    private String username;
    private double currentBalance;
    private double totalInvested;
    private double totalWithdrawn;
    private double portfolioValue;
    private double totalGain;
    private double gainPercentage;
    private List<AssetSummary> assets;

    public PortfolioSummary() {
    }

    public PortfolioSummary(String username, double currentBalance, double totalInvested,
                            double totalWithdrawn, double portfolioValue, double totalGain,
                            double gainPercentage, List<AssetSummary> assets) {
        this.username = username;
        this.currentBalance = currentBalance;
        this.totalInvested = totalInvested;
        this.totalWithdrawn = totalWithdrawn;
        this.portfolioValue = portfolioValue;
        this.totalGain = totalGain;
        this.gainPercentage = gainPercentage;
        this.assets = assets;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public double getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(double currentBalance) { this.currentBalance = currentBalance; }

    public double getTotalInvested() { return totalInvested; }
    public void setTotalInvested(double totalInvested) { this.totalInvested = totalInvested; }

    public double getTotalWithdrawn() { return totalWithdrawn; }
    public void setTotalWithdrawn(double totalWithdrawn) { this.totalWithdrawn = totalWithdrawn; }

    public double getPortfolioValue() { return portfolioValue; }
    public void setPortfolioValue(double portfolioValue) { this.portfolioValue = portfolioValue; }

    public double getTotalGain() { return totalGain; }
    public void setTotalGain(double totalGain) { this.totalGain = totalGain; }

    public double getGainPercentage() { return gainPercentage; }
    public void setGainPercentage(double gainPercentage) { this.gainPercentage = gainPercentage; }

    public List<AssetSummary> getAssets() { return assets; }
    public void setAssets(List<AssetSummary> assets) { this.assets = assets; }
}
