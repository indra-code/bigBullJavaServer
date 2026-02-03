package com.BigBull.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.BigBull.dto.BuyAssetRequest;
import com.BigBull.dto.SellAssetRequest;
import com.BigBull.entity.Asset;
import com.BigBull.entity.Transaction;
import com.BigBull.entity.Wallet;
import com.BigBull.repository.AssetRepository;
import com.BigBull.repository.TransactionRepository;
import com.BigBull.repository.WalletRepository;

@Service
public class PortfolioService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private WalletRepository walletRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String FLASK_BASE_URL = "http://localhost:5000";
    private static final double DEFAULT_PRICE = 0.0;

    @Transactional
    public Map<String, Object> buyAsset(BuyAssetRequest request) {
        // Validate input
        if (request.getUnits() <= 0) {
            throw new IllegalArgumentException("Units must be greater than 0");
        }
        if (request.getPricePerUnit() <= 0) {
            throw new IllegalArgumentException("Price per unit must be greater than 0");
        }

        // Get wallet
        Wallet wallet = walletRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + request.getUsername()));

        double totalCost = request.getUnits() * request.getPricePerUnit();

        // Check balance
        if (wallet.getBalance() < totalCost) {
            throw new RuntimeException("Insufficient balance. Required: " + totalCost + ", Available: " + wallet.getBalance());
        }

        // Find or create asset
        Asset asset = assetRepository.findBySymbol(request.getSymbol())
                .orElseGet(() -> {
                    Asset newAsset = new Asset();
                    newAsset.setSymbol(request.getSymbol());
                    newAsset.setName(request.getName());
                    newAsset.setType("STOCK");
                    return assetRepository.save(newAsset);
                });

        // Deduct from wallet
        wallet.setBalance(wallet.getBalance() - totalCost);
        wallet.setTotalInvested(wallet.getTotalInvested() + totalCost);
        walletRepository.save(wallet);

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setUsername(request.getUsername());
        transaction.setAsset(asset);
        transaction.setType("BUY");
        transaction.setUnits(request.getUnits());
        transaction.setPricePerUnit(request.getPricePerUnit());
        transaction.setTotalAmount(totalCost);
        transaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction);

        return Map.of(
                "success", true,
                "message", "Successfully bought " + request.getUnits() + " units of " + request.getSymbol() + " at $" + request.getPricePerUnit(),
                "asset", Map.of(
                        "id", asset.getId(),
                        "symbol", asset.getSymbol(),
                        "name", asset.getName(),
                        "units", request.getUnits()
                ),
                "transaction", Map.of(
                        "id", transaction.getId(),
                        "type", "BUY",
                        "totalAmount", totalCost,
                        "timestamp", transaction.getTransactionDate()
                ),
                "remainingBalance", wallet.getBalance()
        );
    }

    @Transactional
    public Map<String, Object> sellAsset(SellAssetRequest request) {
        // Validate input
        if (request.getUnits() <= 0) {
            throw new IllegalArgumentException("Units must be greater than 0");
        }
        if (request.getPricePerUnit() <= 0) {
            throw new IllegalArgumentException("Price per unit must be greater than 0");
        }

        // Get wallet
        Wallet wallet = walletRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + request.getUsername()));

        // Get asset
        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new RuntimeException("Asset not found with id: " + request.getAssetId()));

        // Calculate total holdings
        List<Transaction> userTransactions = transactionRepository.findByUsernameAndAsset(request.getUsername(), asset);

        int totalHoldings = userTransactions.stream()
                .mapToInt(t -> t.getType().equals("BUY") ? t.getUnits() : -t.getUnits())
                .sum();

        if (totalHoldings < request.getUnits()) {
            throw new RuntimeException("Insufficient holdings. You have " + totalHoldings + " units, trying to sell " + request.getUnits());
        }

        double totalRevenue = request.getUnits() * request.getPricePerUnit();

        // Add to wallet
        wallet.setBalance(wallet.getBalance() + totalRevenue);
        wallet.setTotalWithdrawn(wallet.getTotalWithdrawn() + totalRevenue);
        walletRepository.save(wallet);

        // Create sell transaction
        Transaction transaction = new Transaction();
        transaction.setUsername(request.getUsername());
        transaction.setAsset(asset);
        transaction.setType("SELL");
        transaction.setUnits(request.getUnits());
        transaction.setPricePerUnit(request.getPricePerUnit());
        transaction.setTotalAmount(totalRevenue);
        transaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction);

        return Map.of(
                "success", true,
                "message", "Successfully sold " + request.getUnits() + " units of " + asset.getSymbol() + " at $" + request.getPricePerUnit(),
                "saleProceeds", totalRevenue,
                "remainingUnits", totalHoldings - request.getUnits(),
                "transaction", Map.of(
                        "id", transaction.getId(),
                        "type", "SELL",
                        "totalAmount", totalRevenue,
                        "timestamp", transaction.getTransactionDate()
                ),
                "newBalance", wallet.getBalance()
        );
    }

    public Map<String, Object> getPortfolioSummary(String username) {
        List<Transaction> userTransactions = transactionRepository.findByUsername(username);

        if (userTransactions.isEmpty()) {
            return Map.of(
                    "username", username,
                    "totalInvested", 0.0,
                    "totalCurrentValue", 0.0,
                    "totalPnl", 0.0,
                    "totalPnlPercentage", 0.0,
                    "holdings", new ArrayList<>(),
                    "timestamp", System.currentTimeMillis()
            );
        }

        // Group transactions by asset
        Map<Asset, List<Transaction>> transactionsByAsset = new HashMap<>();
        for (Transaction t : userTransactions) {
            transactionsByAsset.computeIfAbsent(t.getAsset(), k -> new ArrayList<>()).add(t);
        }

        double totalInvested = 0.0;
        double totalCurrentValue = 0.0;
        List<Map<String, Object>> holdings = new ArrayList<>();

        for (Map.Entry<Asset, List<Transaction>> entry : transactionsByAsset.entrySet()) {
            Asset asset = entry.getKey();
            List<Transaction> transactions = entry.getValue();

            // Calculate total units and cost
            int totalUnits = 0;
            double totalCost = 0.0;

            for (Transaction t : transactions) {
                if ("BUY".equalsIgnoreCase(t.getType())) {
                    totalUnits += t.getUnits();
                    totalCost += t.getUnits() * t.getPricePerUnit();
                } else if ("SELL".equalsIgnoreCase(t.getType())) {
                    totalUnits -= t.getUnits();
                    totalCost -= t.getUnits() * t.getPricePerUnit();
                }
            }

            if (totalUnits > 0) {
                double avgPrice = totalCost / totalUnits;
                double invested = avgPrice * totalUnits;

                // Get current price with error handling
                double currentPrice = getCurrentPrice(asset.getSymbol());
                double currentValue = currentPrice * totalUnits;
                double pnl = currentValue - invested;
                double pnlPercentage = invested > 0 ? (pnl / invested) * 100 : 0;

                totalInvested += invested;
                totalCurrentValue += currentValue;

                Map<String, Object> holding = new HashMap<>();
                holding.put("id", asset.getId());
                holding.put("symbol", asset.getSymbol());
                holding.put("name", asset.getName());
                holding.put("units", totalUnits);
                holding.put("avgPrice", round(avgPrice, 2));
                holding.put("currentPrice", round(currentPrice, 2));
                holding.put("invested", round(invested, 2));
                holding.put("currentValue", round(currentValue, 2));
                holding.put("pnl", round(pnl, 2));
                holding.put("pnlPercentage", round(pnlPercentage, 2));
                holding.put("priceStatus", currentPrice == DEFAULT_PRICE ? "unavailable" : "available");

                holdings.add(holding);
            }
        }

        double totalPnl = totalCurrentValue - totalInvested;
        double totalPnlPercentage = totalInvested > 0 ? (totalPnl / totalInvested) * 100 : 0;

        Wallet wallet = walletRepository.findByUsername(username)
                .orElse(new Wallet());

        Map<String, Object> summary = new HashMap<>();
        summary.put("username", username);
        summary.put("totalInvested", round(totalInvested, 2));
        summary.put("totalCurrentValue", round(totalCurrentValue, 2));
        summary.put("totalPnl", round(totalPnl, 2));
        summary.put("totalPnlPercentage", round(totalPnlPercentage, 2));
        summary.put("walletBalance", round(wallet.getBalance(), 2));
        summary.put("totalBalance", round(wallet.getBalance() + totalCurrentValue, 2));
        summary.put("holdings", holdings);
        summary.put("timestamp", System.currentTimeMillis());

        return summary;
    }

    private double getCurrentPrice(String symbol) {
        try {
            String url = FLASK_BASE_URL + "/api/stock/quote/" + symbol;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("price")) {
                Object price = response.get("price");
                if (price instanceof Number) {
                    return ((Number) price).doubleValue();
                }
            }
        } catch (RestClientException e) {
            System.err.println("Error fetching price for " + symbol + " from Flask API: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error fetching price for " + symbol + ": " + e.getMessage());
        }

        return DEFAULT_PRICE;
    }

    private double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
