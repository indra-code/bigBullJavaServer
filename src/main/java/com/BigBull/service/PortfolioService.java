package com.BigBull.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.BigBull.entity.Transaction;
import com.BigBull.repository.TransactionRepository;

@Service
public class PortfolioService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String FLASK_BASE_URL = "http://localhost:5000";
    
    public Map<String, Object> getPortfolioSummary() {
        List<Transaction> allTransactions = transactionRepository.findAll();
        
        // Group transactions by symbol
        Map<String, List<Transaction>> transactionsBySymbol = new HashMap<>();
        for (Transaction t : allTransactions) {
            String symbol = t.getAsset().getSymbol();
            transactionsBySymbol.computeIfAbsent(symbol, k -> new ArrayList<>()).add(t);
        }
        
        double totalInvested = 0.0;
        double totalCurrentValue = 0.0;
        List<Map<String, Object>> holdings = new ArrayList<>();
        
        for (Map.Entry<String, List<Transaction>> entry : transactionsBySymbol.entrySet()) {
            String symbol = entry.getKey();
            List<Transaction> transactions = entry.getValue();
            
            // Calculate average price and total quantity using average price method
            double totalQuantity = 0.0;
            double totalCost = 0.0;
            
            for (Transaction t : transactions) {
                if ("BUY".equalsIgnoreCase(t.getType())) {
                    totalQuantity += t.getQuantity();
                    totalCost += t.getQuantity() * t.getPrice();
                } else if ("SELL".equalsIgnoreCase(t.getType())) {
                    totalQuantity -= t.getQuantity();
                    totalCost -= t.getQuantity() * t.getPrice();
                }
            }
            
            if (totalQuantity > 0) {
                double avgPrice = totalCost / totalQuantity;
                double invested = avgPrice * totalQuantity;
                
                // Get current price from Flask server
                double currentPrice = getCurrentPrice(symbol);
                double currentValue = currentPrice * totalQuantity;
                double pnl = currentValue - invested;
                double pnlPercentage = (pnl / invested) * 100;
                
                totalInvested += invested;
                totalCurrentValue += currentValue;
                
                Map<String, Object> holding = new HashMap<>();
                holding.put("symbol", symbol);
                holding.put("name", transactions.get(0).getAsset().getName());
                holding.put("quantity", totalQuantity);
                holding.put("avgPrice", avgPrice);
                holding.put("currentPrice", currentPrice);
                holding.put("invested", invested);
                holding.put("currentValue", currentValue);
                holding.put("pnl", pnl);
                holding.put("pnlPercentage", pnlPercentage);
                
                holdings.add(holding);
            }
        }
        
        double totalPnl = totalCurrentValue - totalInvested;
        double totalPnlPercentage = totalInvested > 0 ? (totalPnl / totalInvested) * 100 : 0;
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalInvested", totalInvested);
        summary.put("totalCurrentValue", totalCurrentValue);
        summary.put("totalPnl", totalPnl);
        summary.put("totalPnlPercentage", totalPnlPercentage);
        summary.put("holdings", holdings);
        
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
        } catch (Exception e) {
            System.err.println("Error fetching price for " + symbol + ": " + e.getMessage());
        }
        return 0.0;
    }
}
