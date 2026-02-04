package com.BigBull.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import com.BigBull.entity.Transaction;
import com.BigBull.exception.MarketDataUnavailableException;
import com.BigBull.exception.PriceDataNotFoundException;
import com.BigBull.repository.TransactionRepository;

@Service
public class PortfolioService {
    
    private static final Logger logger = LoggerFactory.getLogger(PortfolioService.class);
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String FLASK_BASE_URL = "http://localhost:5000";
    
    public Map<String, Object> getPortfolioSummary() {
        logger.debug("Retrieving portfolio summary");
        try {
            List<Transaction> allTransactions = transactionRepository.findAll();
            logger.debug("Total transactions retrieved: {}", allTransactions.size());
            
            // Group transactions by symbol
            Map<String, List<Transaction>> transactionsBySymbol = new HashMap<>();
            for (Transaction t : allTransactions) {
                String symbol = t.getAsset().getSymbol();
                transactionsBySymbol.computeIfAbsent(symbol, k -> new ArrayList<>()).add(t);
            }
            logger.debug("Transactions grouped by symbol. Total unique symbols: {}", transactionsBySymbol.size());
            
            double totalInvested = 0.0;
            double totalCurrentValue = 0.0;
            List<Map<String, Object>> holdings = new ArrayList<>();
            
            for (Map.Entry<String, List<Transaction>> entry : transactionsBySymbol.entrySet()) {
                String symbol = entry.getKey();
                List<Transaction> transactions = entry.getValue();
                logger.debug("Processing symbol: {} with {} transactions", symbol, transactions.size());
                
                // Calculate average price and total quantity using average price method
                double totalQuantity = 0.0;
                double totalCost = 0.0;
                
                for (Transaction t : transactions) {
                    if ("BUY".equalsIgnoreCase(t.getType())) {
                        totalQuantity += t.getQuantity();
                        totalCost += t.getQuantity() * t.getPrice();
                        logger.debug("BUY transaction - Symbol: {}, Qty: {}, Price: ${}", 
                            symbol, t.getQuantity(), t.getPrice());
                    } else if ("SELL".equalsIgnoreCase(t.getType())) {
                        totalQuantity -= t.getQuantity();
                        totalCost -= t.getQuantity() * t.getPrice();
                        logger.debug("SELL transaction - Symbol: {}, Qty: {}, Price: ${}", 
                            symbol, t.getQuantity(), t.getPrice());
                    }
                }
                
                if (totalQuantity > 0) {
                    double avgPrice = totalCost / totalQuantity;
                    double invested = avgPrice * totalQuantity;
                    
                    logger.debug("Calculating portfolio for symbol: {}. Avg Price: ${}, Total Qty: {}, Invested: ${}", 
                        symbol, avgPrice, totalQuantity, invested);
                    
                    // Get current price from Flask server
                    double currentPrice = getCurrentPrice(symbol);
                    double currentValue = currentPrice * totalQuantity;
                    double pnl = currentValue - invested;
                    double pnlPercentage = (pnl / invested) * 100;
                    
                    totalInvested += invested;
                    totalCurrentValue += currentValue;
                    
                    logger.info("Portfolio holding calculated - Symbol: {}, Current Price: ${}, Current Value: ${}, P&L: ${}, P&L %: {}%", 
                        symbol, currentPrice, currentValue, pnl, String.format("%.2f", pnlPercentage));
                    
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
            
            logger.info("Portfolio summary calculated - Total Invested: ${}, Total Current Value: ${}, Total P&L: ${}, Total P&L %: {}%", 
                totalInvested, totalCurrentValue, totalPnl, String.format("%.2f", totalPnlPercentage));
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalInvested", totalInvested);
            summary.put("totalCurrentValue", totalCurrentValue);
            summary.put("totalPnl", totalPnl);
            summary.put("totalPnlPercentage", totalPnlPercentage);
            summary.put("holdings", holdings);
            
            logger.debug("Portfolio summary completed with {} holdings", holdings.size());
            return summary;
        } catch (Exception e) {
            logger.error("Error retrieving portfolio summary", e);
            throw e;
        }
    }
    
    private double getCurrentPrice(String symbol) {
        logger.debug("Fetching current price for symbol: {}", symbol);
        try {
            String url = FLASK_BASE_URL + "/api/stock/quote/" + symbol;
            logger.debug("Calling Flask API: {}", url);
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.containsKey("price")) {
                Object price = response.get("price");
                if (price instanceof Number) {
                    double priceValue = ((Number) price).doubleValue();
                    logger.info("Current price retrieved for symbol: {}, Price: ${}", symbol, priceValue);
                    return priceValue;
                } else {
                    logger.warn("Invalid price format for symbol: {}", symbol);
                    throw new PriceDataNotFoundException(
                        "Invalid price format received for symbol: " + symbol, 
                        symbol
                    );
                }
            } else {
                logger.error("Price data not found in response for symbol: {}", symbol);
                throw new PriceDataNotFoundException(
                    "Price data not found for symbol: " + symbol, 
                    symbol
                );
            }
        } catch (RestClientException e) {
            logger.error("Failed to fetch price from Flask server for symbol: {}. Error: {}", symbol, e.getMessage());
            throw new MarketDataUnavailableException(
                "Market data unavailable for symbol: " + symbol + ". Flask service may be offline.",
                symbol,
                e
            );
        } catch (PriceDataNotFoundException e) {
            logger.error("Price data not found for symbol: {}", symbol, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error fetching price for symbol: {}", symbol, e);
            throw new MarketDataUnavailableException(
                "Error fetching market data for symbol: " + symbol,
                symbol,
                e
            );
        }
    }
}
