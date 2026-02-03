package com.BigBull.controller;

import com.BigBull.dto.WalletDTO;
import com.BigBull.entity.Wallet;
import com.BigBull.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "*")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping("/create")
    public ResponseEntity<?> createWallet(@RequestParam String username, @RequestParam Double initialBalance) {
        try {
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Username cannot be empty");
            }
            if (initialBalance == null || initialBalance < 0) {
                return ResponseEntity.badRequest().body("Initial balance must be non-negative");
            }
            Wallet wallet = walletService.createWallet(username, initialBalance);
            return ResponseEntity.ok(convertToDTO(wallet));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getWallet(@PathVariable String username) {
        try {
            Wallet wallet = walletService.getWalletByUsername(username);
            return ResponseEntity.ok(convertToDTO(wallet));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> depositFunds(@RequestParam String username, @RequestParam Double amount) {
        try {
            if (amount == null || amount <= 0) {
                return ResponseEntity.badRequest().body("Deposit amount must be greater than 0");
            }
            walletService.depositFunds(username, amount);
            Wallet wallet = walletService.getWalletByUsername(username);
            return ResponseEntity.ok(convertToDTO(wallet));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdrawFunds(@RequestParam String username, @RequestParam Double amount) {
        try {
            if (amount == null || amount <= 0) {
                return ResponseEntity.badRequest().body("Withdrawal amount must be greater than 0");
            }
            walletService.withdrawFunds(username, amount);
            Wallet wallet = walletService.getWalletByUsername(username);
            return ResponseEntity.ok(convertToDTO(wallet));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    private WalletDTO convertToDTO(Wallet wallet) {
        return new WalletDTO(
                wallet.getId(),
                wallet.getUsername(),
                wallet.getBalance(),
                wallet.getTotalInvested(),
                wallet.getTotalWithdrawn(),
                wallet.getCreatedAt(),
                wallet.getUpdatedAt()
        );
    }
}
