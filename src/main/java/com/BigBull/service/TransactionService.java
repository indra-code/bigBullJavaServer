package com.BigBull.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.BigBull.entity.Asset;
import com.BigBull.entity.Transaction;
import com.BigBull.exception.AssetNotFoundException;
import com.BigBull.exception.InsufficientAssetException;
import com.BigBull.exception.InsufficientBalanceException;
import com.BigBull.exception.InvalidTransactionTypeException;
import com.BigBull.exception.TransactionFailedException;
import com.BigBull.exception.TransactionNotFoundException;
import com.BigBull.repository.TransactionRepository;

@Service
public class TransactionService {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AssetService assetService;
    
    @Autowired
    private WalletService walletService;
    
    public List<Transaction> getAllTransactions() {
        logger.debug("Fetching all transactions");
        try {
            List<Transaction> transactions = transactionRepository.findAll();
            logger.debug("Retrieved {} transactions from database", transactions.size());
            return transactions;
        } catch (Exception e) {
            logger.error("Error retrieving all transactions", e);
            throw e;
        }
    }
    
    public Optional<Transaction> getTransactionById(Long id) {
        logger.debug("Fetching transaction by ID: {}", id);
        try {
            if (id == null || id <= 0) {
                logger.error("Invalid transaction ID: {}", id);
                throw new TransactionNotFoundException("Invalid transaction ID: " + id, id);
            }
            Optional<Transaction> transaction = transactionRepository.findById(id);
            if (transaction.isPresent()) {
                logger.debug("Transaction found - ID: {}, Type: {}, Quantity: {}", 
                    id, transaction.get().getType(), transaction.get().getQuantity());
            } else {
                logger.warn("Transaction not found with ID: {}", id);
            }
            return transaction;
        } catch (TransactionNotFoundException e) {
            logger.error("Error retrieving transaction by ID: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error retrieving transaction by ID: {}", id, e);
            throw e;
        }
    }
    
    public List<Transaction> getTransactionsByAssetId(Long assetId) {
        logger.debug("Fetching transactions by asset ID: {}", assetId);
        try {
            List<Transaction> transactions = transactionRepository.findByAssetId(assetId);
            logger.debug("Retrieved {} transactions for asset ID: {}", transactions.size(), assetId);
            return transactions;
        } catch (Exception e) {
            logger.error("Error retrieving transactions by asset ID: {}", assetId, e);
            throw e;
        }
    }
    
    public List<Transaction> getTransactionsBySymbol(String symbol) {
        logger.debug("Fetching transactions by symbol: {}", symbol);
        try {
            List<Transaction> transactions = transactionRepository.findByAssetSymbol(symbol);
            logger.debug("Retrieved {} transactions for symbol: {}", transactions.size(), symbol);
            return transactions;
        } catch (Exception e) {
            logger.error("Error retrieving transactions by symbol: {}", symbol, e);
            throw e;
        }
    }
    
    @Transactional
    public Transaction createTransaction(Long assetId, String type, Double quantity, Double price) {
        logger.debug("Creating transaction - Asset ID: {}, Type: {}, Quantity: {}, Price: {}", 
            assetId, type, quantity, price);
        
        try {
            // Validate input parameters
            if (quantity == null || quantity <= 0) {
                logger.error("Invalid transaction quantity: {}", quantity);
                throw new TransactionFailedException(
                    "Transaction quantity must be positive", 
                    "Invalid quantity: " + quantity
                );
            }
            
            if (price == null || price <= 0) {
                logger.error("Invalid transaction price: {}", price);
                throw new TransactionFailedException(
                    "Transaction price must be positive", 
                    "Invalid price: " + price
                );
            }
            
            if (type == null || (!type.equalsIgnoreCase("BUY") && !type.equalsIgnoreCase("SELL"))) {
                logger.error("Invalid transaction type: {}. Must be BUY or SELL", type);
                throw new InvalidTransactionTypeException(
                    "Invalid transaction type. Use 'BUY' or 'SELL'", 
                    type
                );
            }
            
            Optional<Asset> assetOpt = assetService.getAssetById(assetId);
            if (assetOpt.isEmpty()) {
                logger.error("Asset not found with ID: {}", assetId);
                throw new AssetNotFoundException(
                    "Asset not found with ID: " + assetId + ". Please create the asset first.", 
                    assetId
                );
            }
            
            Asset asset = assetOpt.get();
            Double totalAmount = quantity * price;
            logger.debug("Transaction validation - Total Amount: ${}", totalAmount);
            
            // Handle BUY transaction
            if ("BUY".equalsIgnoreCase(type)) {
                logger.debug("Processing BUY transaction - Asset: {}, Quantity: {}, Unit Price: ${}", 
                    asset.getSymbol(), quantity, price);
                
                // Check if wallet has sufficient balance
                if (!walletService.hasBalance(totalAmount)) {
                    Double availableBalance = walletService.getWallet().getBalance();
                    logger.error("Insufficient balance for BUY. Required: ${}, Available: ${}", 
                        totalAmount, availableBalance);
                    throw new InsufficientBalanceException(
                        "Insufficient wallet balance for BUY transaction",
                        totalAmount,
                        availableBalance
                    );
                }
                
                // Deduct from wallet
                walletService.deductBalance(totalAmount);
                logger.debug("Wallet debited with amount: ${}", totalAmount);
                
                // Add quantity to asset
                Double previousQuantity = asset.getQuantity();
                asset.addQuantity(quantity);
                assetService.saveAsset(asset);
                logger.info("Asset quantity updated. Symbol: {}, Previous Qty: {}, New Qty: {}", 
                    asset.getSymbol(), previousQuantity, asset.getQuantity());
            } 
            // Handle SELL transaction
            else if ("SELL".equalsIgnoreCase(type)) {
                logger.debug("Processing SELL transaction - Asset: {}, Quantity: {}, Unit Price: ${}", 
                    asset.getSymbol(), quantity, price);
                
                // Check if asset has sufficient quantity
                if (asset.getQuantity() < quantity) {
                    logger.error("Insufficient asset for SELL. Symbol: {}, Required: {}, Available: {}", 
                        asset.getSymbol(), quantity, asset.getQuantity());
                    throw new InsufficientAssetException(
                        "Insufficient asset quantity for SELL transaction",
                        asset.getSymbol(),
                        quantity,
                        asset.getQuantity()
                    );
                }
                
                // Credit to wallet
                walletService.creditBalance(totalAmount);
                logger.debug("Wallet credited with amount: ${}", totalAmount);
                
                // Remove quantity from asset
                Double previousQuantity = asset.getQuantity();
                asset.removeQuantity(quantity);
                assetService.saveAsset(asset);
                logger.info("Asset quantity updated. Symbol: {}, Previous Qty: {}, New Qty: {}", 
                    asset.getSymbol(), previousQuantity, asset.getQuantity());
                
                // Delete asset if quantity becomes 0
                if (asset.getQuantity() == 0.0) {
                    logger.info("Asset quantity is zero. Deleting asset with ID: {} and Symbol: {}", 
                        asset.getId(), asset.getSymbol());
                    assetService.deleteAsset(asset.getId());
                }
            }
            
            Transaction transaction = new Transaction(
                asset,
                type,
                quantity,
                price,
                LocalDateTime.now()
            );
            Transaction savedTransaction = transactionRepository.save(transaction);
            logger.info("Transaction created successfully. ID: {}, Type: {}, Asset: {}, Quantity: {}, Price: ${}, Total: ${}", 
                savedTransaction.getId(), type, asset.getSymbol(), quantity, price, totalAmount);
            return savedTransaction;
        } catch (AssetNotFoundException | InvalidTransactionTypeException | 
                 InsufficientBalanceException | InsufficientAssetException e) {
            logger.error("Transaction validation error", e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error creating transaction", e);
            throw new TransactionFailedException(
                "Failed to create transaction: " + e.getMessage(),
                e.getMessage(),
                e
            );
        }
    }
    
    // New method to buy stock with symbol (creates asset if doesn't exist)
    @Transactional
    public Transaction buyStock(String symbol, String name, Double quantity, Double price) {
        logger.debug("Buy stock operation initiated - Symbol: {}, Name: {}, Quantity: {}, Price: ${}", 
            symbol, name, quantity, price);
        
        try {
            // Validate input parameters
            if (symbol == null || symbol.trim().isEmpty()) {
                logger.error("Invalid symbol provided for buyStock: {}", symbol);
                throw new AssetNotFoundException("Invalid stock symbol: " + symbol, null, symbol);
            }
            
            if (quantity == null || quantity <= 0) {
                logger.error("Invalid quantity for buyStock: {}", quantity);
                throw new TransactionFailedException("Invalid quantity: " + quantity, "Quantity must be positive");
            }
            
            if (price == null || price <= 0) {
                logger.error("Invalid price for buyStock: {}", price);
                throw new TransactionFailedException("Invalid price: " + price, "Price must be positive");
            }
            
            Double totalAmount = quantity * price;
            logger.info("Processing buy transaction - Symbol: {}, Quantity: {}, Unit Price: ${}, Total: ${}", 
                symbol, quantity, price, totalAmount);
            
            // Check if wallet has sufficient balance
            if (!walletService.hasBalance(totalAmount)) {
                Double availableBalance = walletService.getWallet().getBalance();
                logger.error("Insufficient balance for buyStock. Symbol: {}, Required: ${}, Available: ${}", 
                    symbol, totalAmount, availableBalance);
                throw new InsufficientBalanceException(
                    "Insufficient wallet balance for buying " + symbol,
                    totalAmount,
                    availableBalance
                );
            }
            logger.debug("Wallet balance verified for symbol: {}", symbol);
            
            // Get or create asset
            Asset asset = assetService.getAssetBySymbol(symbol)
                .orElseGet(() -> {
                    logger.info("Asset not found. Creating new asset - Symbol: {}, Name: {}", symbol, name);
                    return assetService.createAsset("STOCK", name, symbol);
                });
            
            logger.debug("Asset details - ID: {}, Symbol: {}, Current Quantity: {}", 
                asset.getId(), asset.getSymbol(), asset.getQuantity());
            
            // Ensure asset has an ID (is persisted)
            if (asset.getId() == null) {
                asset = assetService.saveAsset(asset);
                logger.info("New asset persisted to database - ID: {}", asset.getId());
            }
            
            // Deduct from wallet
            walletService.deductBalance(totalAmount);
            logger.info("Wallet debited: ${} for stock {}", totalAmount, symbol);
            
            // Update cost per unit using weighted average
            Double previousQty = asset.getQuantity();
            Double previousCost = asset.getCostPerUnit();
            asset.updateCostPerUnit(quantity, price);
            logger.debug("Cost per unit calculation - Symbol: {}, Previous Cost: ${}, New Cost: ${}", 
                symbol, previousCost, asset.getCostPerUnit());
            
            // Add quantity to asset
            asset.addQuantity(quantity);
            asset = assetService.saveAsset(asset);
            logger.info("Asset quantity updated - Symbol: {}, Previous Qty: {}, New Qty: {}", 
                symbol, previousQty, asset.getQuantity());
            
            // Create transaction
            Transaction transaction = new Transaction(
                asset,
                "BUY",
                quantity,
                price,
                LocalDateTime.now()
            );
            
            Transaction savedTransaction = transactionRepository.save(transaction);
            logger.info("Buy transaction completed successfully - Transaction ID: {}, Asset: {}, Quantity: {}", 
                savedTransaction.getId(), symbol, quantity);
            return savedTransaction;
        } catch (AssetNotFoundException | InsufficientBalanceException | TransactionFailedException e) {
            logger.error("Buy transaction failed for symbol: {}", symbol, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in buyStock operation for symbol: {}", symbol, e);
            throw new TransactionFailedException(
                "Failed to complete buy transaction for " + symbol + ": " + e.getMessage(),
                e.getMessage(),
                e
            );
        }
    }
    
    // New method to sell stock by symbol
    @Transactional
    public Transaction sellStock(String symbol, Double quantity, Double price) {
        logger.debug("Sell stock operation initiated - Symbol: {}, Quantity: {}, Price: ${}", 
            symbol, quantity, price);
        
        try {
            // Validate input parameters
            if (symbol == null || symbol.trim().isEmpty()) {
                logger.error("Invalid symbol provided for sellStock: {}", symbol);
                throw new AssetNotFoundException("Invalid stock symbol: " + symbol, null, symbol);
            }
            
            if (quantity == null || quantity <= 0) {
                logger.error("Invalid quantity for sellStock: {}", quantity);
                throw new TransactionFailedException("Invalid quantity: " + quantity, "Quantity must be positive");
            }
            
            if (price == null || price <= 0) {
                logger.error("Invalid price for sellStock: {}", price);
                throw new TransactionFailedException("Invalid price: " + price, "Price must be positive");
            }
            
            Optional<Asset> assetOpt = assetService.getAssetBySymbol(symbol);
            if (assetOpt.isEmpty()) {
                logger.error("Asset not found for sellStock with symbol: {}", symbol);
                throw new AssetNotFoundException(
                    "Asset not found with symbol: " + symbol, 
                    null, 
                    symbol
                );
            }
            
            Asset asset = assetOpt.get();
            Double totalAmount = quantity * price;
            logger.info("Processing sell transaction - Symbol: {}, Asset ID: {}, Quantity: {}, Unit Price: ${}, Total: ${}", 
                symbol, asset.getId(), quantity, price, totalAmount);
            
            // Check if asset has sufficient quantity
            if (asset.getQuantity() < quantity) {
                logger.error("Insufficient asset quantity for sellStock. Symbol: {}, Required: {}, Available: {}", 
                    symbol, quantity, asset.getQuantity());
                throw new InsufficientAssetException(
                    "Insufficient asset quantity for SELL transaction",
                    symbol,
                    quantity,
                    asset.getQuantity()
                );
            }
            logger.debug("Asset quantity verified for symbol: {}", symbol);
            
            // Credit to wallet
            walletService.creditBalance(totalAmount);
            logger.info("Wallet credited: ${} from selling {}", totalAmount, symbol);
            
            // Remove quantity from asset and save it
            Double previousQuantity = asset.getQuantity();
            asset.removeQuantity(quantity);
            asset = assetService.saveAsset(asset);
            logger.info("Asset quantity updated - Symbol: {}, Previous Qty: {}, New Qty: {}", 
                symbol, previousQuantity, asset.getQuantity());
            
            // Create transaction with the managed asset reference
            Transaction transaction = new Transaction(
                asset,
                "SELL",
                quantity,
                price,
                LocalDateTime.now()
            );
            Transaction savedTransaction = transactionRepository.save(transaction);
            logger.info("Sell transaction recorded - Transaction ID: {}, Asset: {}, Quantity: {}", 
                savedTransaction.getId(), symbol, quantity);
            
            // Delete asset if quantity becomes 0 (AFTER transaction is saved)
            if (asset.getQuantity() == 0.0) {
                logger.info("Asset quantity is zero after sell. Deleting asset - Symbol: {}, ID: {}", 
                    symbol, asset.getId());
                assetService.deleteAsset(asset.getId());
            }
            
            logger.info("Sell transaction completed successfully - Symbol: {}, Quantity: {}", symbol, quantity);
            return savedTransaction;
        } catch (AssetNotFoundException | InsufficientAssetException | TransactionFailedException e) {
            logger.error("Sell transaction failed for symbol: {}", symbol, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in sellStock operation for symbol: {}", symbol, e);
            throw new TransactionFailedException(
                "Failed to complete sell transaction for " + symbol + ": " + e.getMessage(),
                e.getMessage(),
                e
            );
        }
    }
    
    public void deleteTransaction(Long id) {
        logger.debug("Deleting transaction with ID: {}", id);
        try {
            if (id == null || id <= 0) {
                logger.error("Invalid transaction ID for deletion: {}", id);
                throw new TransactionNotFoundException("Invalid transaction ID: " + id, id);
            }
            transactionRepository.deleteById(id);
            logger.info("Transaction deleted successfully with ID: {}", id);
        } catch (TransactionNotFoundException e) {
            logger.error("Error deleting transaction with ID: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error deleting transaction with ID: {}", id, e);
            throw e;
        }
    }
}

