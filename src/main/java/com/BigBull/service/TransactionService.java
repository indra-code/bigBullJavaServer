package com.BigBull.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.BigBull.entity.Asset;
import com.BigBull.entity.Transaction;
import com.BigBull.repository.TransactionRepository;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AssetService assetService;
    
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
    
    public Transaction createTransaction(Long assetId, String type, Double quantity, Double price) {
        Optional<Asset> assetOpt = assetService.getAssetById(assetId);
        if (assetOpt.isEmpty()) {
            throw new RuntimeException("Asset not found");
        }
        
        Transaction transaction = new Transaction(
            assetOpt.get(),
            type,
            quantity,
            price,
            LocalDateTime.now()
        );
        return transactionRepository.save(transaction);
    }
    
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
}
