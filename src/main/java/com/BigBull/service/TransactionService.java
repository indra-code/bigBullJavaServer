package com.BigBull.service;

import com.BigBull.dto.TransactionResponse;
import com.BigBull.entity.Asset;
import com.BigBull.entity.Transaction;
import com.BigBull.entity.Wallet;
import com.BigBull.repository.AssetRepository;
import com.BigBull.repository.TransactionRepository;
import com.BigBull.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private org.springframework.web.client.RestTemplate restTemplate;

    private static final String PYTHON_API_URL = "http://localhost:5000/api";

    @Transactional
    public TransactionResponse executeTransaction(String username, String symbol, String type, int units) {
        try {
            // 1. Fetch asset
            Asset asset = assetRepository.findBySymbol(symbol)
                    .orElseThrow(() -> new RuntimeException("Asset not found: " + symbol));

            // 2. Fetch live price from Python API
            double livePrice = fetchLivePriceFromPython(symbol, asset.getType());

            // 3. Fetch wallet
            Wallet wallet = walletRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Wallet not found: " + username));

            // 4. Calculate transaction amount
            double totalAmount = livePrice * units;

            // 5. Process BUY or SELL
            if ("BUY".equalsIgnoreCase(type)) {
                // Check if wallet has enough balance
                if (wallet.getBalance() < totalAmount) {
                    throw new RuntimeException("Insufficient balance. Required: " + totalAmount + ", Available: " + wallet.getBalance());
                }

                // Deduct from wallet
                wallet.setBalance(wallet.getBalance() - totalAmount);
                wallet.setTotalInvested(wallet.getTotalInvested() + totalAmount);

                // Update asset quantity and cost
                double currentQuantity = asset.getQuantity() != null ? asset.getQuantity() : 0;
                double currentCost = asset.getCostPerUnit() != null ? asset.getCostPerUnit() : 0;

                if (currentQuantity == 0) {
                    // First purchase
                    asset.setCostPerUnit(livePrice);
                    asset.setQuantity((double) units);
                } else {
                    // Calculate weighted average cost
                    double totalValue = (currentQuantity * currentCost) + (units * livePrice);
                    double newQuantity = currentQuantity + units;
                    double newAvgCost = totalValue / newQuantity;

                    asset.setCostPerUnit(newAvgCost);
                    asset.setQuantity(newQuantity);
                }

            } else if ("SELL".equalsIgnoreCase(type)) {
                double currentQuantity = asset.getQuantity() != null ? asset.getQuantity() : 0;

                // Check if asset has enough units
                if (currentQuantity < units) {
                    throw new RuntimeException("Insufficient units. Required: " + units + ", Available: " + currentQuantity);
                }

                // Add to wallet
                wallet.setBalance(wallet.getBalance() + totalAmount);
                wallet.setTotalWithdrawn(wallet.getTotalWithdrawn() + totalAmount);

                // Update asset quantity
                asset.setQuantity(currentQuantity - units);

            } else {
                throw new RuntimeException("Invalid transaction type: " + type);
            }

            // 6. Save updated entities
            wallet.setUpdatedAt(LocalDateTime.now());
            walletRepository.save(wallet);
            asset.setUpdatedAt(LocalDateTime.now());
            assetRepository.save(asset);

            // 7. Create and save transaction
            Transaction transaction = new Transaction();
            transaction.setUsername(username);
            transaction.setAsset(asset);
            transaction.setType(type.toUpperCase());
            transaction.setUnits(units);
            transaction.setQuantity((double) units);
            transaction.setPrice(livePrice);
            transaction.setPricePerUnit(livePrice);
            transaction.setTotalAmount(totalAmount);
            transaction.setTransactionDate(LocalDateTime.now());

            transaction = transactionRepository.save(transaction);

            // 8. Return response
            return new TransactionResponse(
                    transaction.getId(),
                    username,
                    asset,
                    type.toUpperCase(),
                    units,
                    livePrice,
                    totalAmount,
                    transaction.getTransactionDate().toString(),
                    wallet.getBalance(),
                    asset.getQuantity(),
                    String.format("Transaction successful. %d units of %s %sed at %.2f",
                            units, symbol, type.toLowerCase(), livePrice)
            );

        } catch (Exception e) {
            throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
        }
    }

    private double fetchLivePriceFromPython(String symbol, String assetType) {
        try {
            String endpoint = (assetType != null && assetType.equalsIgnoreCase("CRYPTO"))
                    ? "/crypto/quote/" + symbol
                    : "/stock/quote/" + symbol;

            String url = PYTHON_API_URL + endpoint;

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (response.getBody() != null) {
                Map<String, Object> data = response.getBody();
                Object priceObj = data.get("price");

                if (priceObj != null) {
                    if (priceObj instanceof Number) {
                        return ((Number) priceObj).doubleValue();
                    } else if (priceObj instanceof String) {
                        return Double.parseDouble((String) priceObj);
                    }
                }
            }

            throw new RuntimeException("Failed to fetch price for: " + symbol);

        } catch (Exception e) {
            throw new RuntimeException("Python API Error for " + symbol + ": " + e.getMessage(), e);
        }
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public List<Transaction> getTransactionsByUsername(String username) {
        return transactionRepository.findByUsername(username);
    }

    public List<Transaction> getTransactionsByAssetId(Long assetId) {
        return transactionRepository.findByAssetId(assetId);
    }
}
