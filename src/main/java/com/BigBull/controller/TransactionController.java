package com.BigBull.controller;

import com.BigBull.entity.Transaction;
import com.BigBull.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<?> executeTransaction(
            @RequestParam String username,
            @RequestParam String symbol,
            @RequestParam String type,
            @RequestParam int units) {
        try {
            Transaction transaction = transactionService.executeTransaction(username, symbol, type, units);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Transaction>> getTransactionsByUsername(@PathVariable String username) {
        return ResponseEntity.ok(transactionService.getTransactionsByUsername(username));
    }

    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<Transaction>> getTransactionsByAssetId(@PathVariable Long assetId) {
        return ResponseEntity.ok(transactionService.getTransactionsByAssetId(assetId));
    }

    // Inner class for error response
    public static class ErrorResponse {
        public String error;
        public long timestamp;

        public ErrorResponse(String error) {
            this.error = error;
            this.timestamp = System.currentTimeMillis();
        }

        public String getError() {
            return error;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
