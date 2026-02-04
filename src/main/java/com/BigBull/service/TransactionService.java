package com.BigBull.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.BigBull.entity.Asset;
import com.BigBull.entity.Transaction;
import com.BigBull.repository.TransactionRepository;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AssetService assetService;
    
    @Autowired
    private WalletService walletService;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    // Fetch stock type from Flask API
    private String fetchStockType(String symbol) {
        try {
            String url = "http://localhost:5000/api/stock/info/" + symbol;
            System.out.println("[API] Fetching stock info from: " + url);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.containsKey("info")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> info = (Map<String, Object>) response.get("info");
                
                if (info != null && info.containsKey("quoteType")) {
                    String quoteType = info.get("quoteType").toString();
                    System.out.println("[API] Quote type retrieved: " + quoteType);
                    return quoteType;
                }
            }
            
            System.out.println("[API] quoteType not found, using default: STOCK");
            return "STOCK";
        } catch (Exception e) {
            System.err.println("[API] Error fetching stock info: " + e.getMessage());
            return "STOCK"; // Default fallback
        }
    }
    
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }
    
    public List<Transaction> getTransactionsByAssetId(Long assetId) {
        return transactionRepository.findByAssetId(assetId);
    }
    
    public List<Transaction> getTransactionsBySymbol(String symbol) {
        return transactionRepository.findByAssetSymbol(symbol);
    }
    
    @Transactional()
    public Transaction createTransaction(Long assetId, String type, Double quantity, Double price) {
        Optional<Asset> assetOpt = assetService.getAssetById(assetId);
        if (assetOpt.isEmpty()) {
            throw new RuntimeException("Asset not found with ID: " + assetId + ". Please create the asset first.");
        }
        
        Asset asset = assetOpt.get();
        Double totalAmount = quantity * price;
        
        // Handle BUY transaction
        if ("BUY".equalsIgnoreCase(type)) {
            // Check if wallet has sufficient balance
            if (!walletService.hasBalance(totalAmount)) {
                throw new RuntimeException("Insufficient wallet balance. Required: " + totalAmount + ", Available: " + walletService.getWallet().getBalance());
            }
            
            // Deduct from wallet
            walletService.deductBalance(totalAmount);
            
            // Add quantity to asset
            asset.addQuantity(quantity);
            assetService.saveAsset(asset);
        } 
        // Handle SELL transaction
        else if ("SELL".equalsIgnoreCase(type)) {
            // Check if asset has sufficient quantity
            if (asset.getQuantity() < quantity) {
                throw new RuntimeException("Insufficient asset quantity. Available: " + asset.getQuantity() + ", Requested: " + quantity);
            }
            
            // Credit to wallet
            walletService.creditBalance(totalAmount);
            
            // Remove quantity from asset
            asset.removeQuantity(quantity);
            assetService.saveAsset(asset);
            
            // Delete asset if quantity becomes 0
            if (asset.getQuantity() == 0.0) {
                assetService.deleteAsset(asset.getId());
            }
        } else {
            throw new RuntimeException("Invalid transaction type. Use 'BUY' or 'SELL'");
        }
        
        Transaction transaction = new Transaction(
            asset,
            type,
            quantity,
            price,
            LocalDateTime.now()
        );
        return transactionRepository.save(transaction);
    }
    
    // New method to buy stock with symbol (creates asset if doesn't exist)
    @Transactional
    public Transaction buyStock(String symbol, String name, Double quantity, Double price) {
        try {
            Double totalAmount = quantity * price;
            
            System.out.println("\n[TransactionService] Processing Buy Transaction...");
            System.out.println("Stock Details - Symbol: " + symbol + ", Name: " + name + ", Qty: " + quantity + ", Price: $" + price);
            System.out.println("Total Amount: $" + totalAmount);
            
            // Check if wallet has sufficient balance
            if (!walletService.hasBalance(totalAmount)) {
                throw new RuntimeException("Insufficient wallet balance. Required: " + totalAmount + ", Available: " + walletService.getWallet().getBalance());
            }
            System.out.println("✓ Wallet balance verified");
            
            // Get or create asset
            Asset asset = assetService.getAssetBySymbol(symbol)
                .orElseGet(() -> {
                    System.out.println("[ASSET] Creating new asset for symbol: " + symbol);
                    String assetType = fetchStockType(symbol);
                    System.out.println("[ASSET] Asset type determined: " + assetType);
                    return assetService.createAsset(assetType, name, symbol);
                });
            
            // Log existing or new asset
            if (asset.getId() != null) {
                System.out.println("[ASSET] Existing asset found - ID: " + asset.getId() + ", Current Qty: " + asset.getQuantity());
            } else {
                System.out.println("[ASSET] New asset created, persisting to database...");
            }
            
            // Ensure asset has an ID (is persisted)
            if (asset.getId() == null) {
                asset = assetService.saveAsset(asset);
                System.out.println("[ASSET] Asset saved with ID: " + asset.getId());
            }
            
            // Deduct from wallet
            walletService.deductBalance(totalAmount);
            System.out.println("✓ Wallet debited: $" + totalAmount);
            
            // Update cost per unit using weighted average
            Double previousQty = asset.getQuantity();
            Double previousCost = asset.getCostPerUnit();
            asset.updateCostPerUnit(quantity, price);
            System.out.println("[ASSET] Cost per unit updated: $" + previousCost + " → $" + asset.getCostPerUnit());
            
            // Add quantity to asset
            asset.addQuantity(quantity);
            asset = assetService.saveAsset(asset);
            System.out.println("[ASSET] Updated asset quantity: " + previousQty + " → " + asset.getQuantity());
            System.out.println("[ASSET] Asset details logged to assets table:");
            System.out.println("  - Asset ID: " + asset.getId());
            System.out.println("  - Type: " + asset.getType());
            System.out.println("  - Name: " + asset.getName());
            System.out.println("  - Symbol: " + asset.getSymbol());
            System.out.println("  - Total Quantity: " + asset.getQuantity());
            System.out.println("  - Cost Per Unit: $" + asset.getCostPerUnit());
            
            // Create transaction
            Transaction transaction = new Transaction(
                asset,
                "BUY",
                quantity,
                price,
                LocalDateTime.now()
            );
            
            Transaction savedTransaction = transactionRepository.save(transaction);
            System.out.println("✓ Transaction record saved with ID: " + savedTransaction.getId());
            System.out.println("[SUCCESS] Buy transaction completed successfully\n");
            return savedTransaction;
        } catch (Exception e) {
            System.err.println("Error in buyStock: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to complete buy transaction: " + e.getMessage(), e);
        }
    }
    
    // New method to sell stock by symbol
    @Transactional
    public Transaction sellStock(String symbol, Double quantity, Double price) {
        Optional<Asset> assetOpt = assetService.getAssetBySymbol(symbol);
        if (assetOpt.isEmpty()) {
            throw new RuntimeException("Asset not found with symbol: " + symbol);
        }
        
        Asset asset = assetOpt.get();
        Double totalAmount = quantity * price;
        
        // Check if asset has sufficient quantity
        if (asset.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient asset quantity. Available: " + asset.getQuantity() + ", Requested: " + quantity);
        }
        
        // Credit to wallet
        walletService.creditBalance(totalAmount);
        
        // Remove quantity from asset and save it
        asset.removeQuantity(quantity);
        asset = assetService.saveAsset(asset); // Use the returned managed entity
        
        // Create transaction with the managed asset reference
        Transaction transaction = new Transaction(
            asset,
            "SELL",
            quantity,
            price,
            LocalDateTime.now()
        );
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Delete asset if quantity becomes 0 (AFTER transaction is saved)
        if (asset.getQuantity() == 0.0) {
            assetService.deleteAsset(asset.getId());
        }
        
        return savedTransaction;
    }
    
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
}
