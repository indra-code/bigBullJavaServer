package com.BigBull.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.BigBull.entity.Transaction;
import com.BigBull.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<Transaction>> getTransactionsByAssetId(@PathVariable Long assetId) {
        return ResponseEntity.ok(transactionService.getTransactionsByAssetId(assetId));
    }
    
    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<List<Transaction>> getTransactionsBySymbol(@PathVariable String symbol) {
        return ResponseEntity.ok(transactionService.getTransactionsBySymbol(symbol));
    }
    
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Map<String, Object> request) {
        Long assetId = Long.valueOf(request.get("assetId").toString());
        String type = request.get("type").toString();
        Double quantity = Double.valueOf(request.get("quantity").toString());
        Double price = Double.valueOf(request.get("price").toString());
        
        Transaction transaction = transactionService.createTransaction(assetId, type, quantity, price);
        return ResponseEntity.ok(transaction);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
