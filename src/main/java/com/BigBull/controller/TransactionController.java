package com.BigBull.controller;

import com.BigBull.dto.TransactionResponse;
import com.BigBull.entity.Transaction;
import com.BigBull.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
            TransactionResponse response = transactionService.executeTransaction(username, symbol, type, units);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse(500, "Internal server error: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        try {
            return ResponseEntity.ok(transactionService.getAllTransactions());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(transactionService.getTransactionById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Transaction>> getTransactionsByUsername(@PathVariable String username) {
        try {
            return ResponseEntity.ok(transactionService.getTransactionsByUsername(username));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<Transaction>> getTransactionsByAssetId(@PathVariable Long assetId) {
        try {
            return ResponseEntity.ok(transactionService.getTransactionsByAssetId(assetId));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    public static class ErrorResponse {
        private int code;
        private String message;

        public ErrorResponse(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
