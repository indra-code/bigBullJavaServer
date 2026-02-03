package com.BigBull.service;

import com.BigBull.entity.Wallet;
import com.BigBull.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    public Wallet createWallet(String username, Double initialBalance) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (initialBalance == null || initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }

        Wallet wallet = new Wallet();
        wallet.setUsername(username);
        wallet.setBalance(initialBalance);
        wallet.setTotalInvested(0.0);
        wallet.setTotalWithdrawn(0.0);
        return walletRepository.save(wallet);
    }

    public Wallet getWalletByUsername(String username) {
        return walletRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Wallet not found for username: " + username));
    }

    public Wallet getWalletById(Long id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + id));
    }

    @Transactional
    public Wallet deductBalance(String username, Double amount) {
        Wallet wallet = getWalletByUsername(username);
        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance. Required: " + amount + ", Available: " + wallet.getBalance());
        }
        wallet.setBalance(wallet.getBalance() - amount);
        wallet.setTotalInvested(wallet.getTotalInvested() + amount);
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet addBalance(String username, Double amount) {
        Wallet wallet = getWalletByUsername(username);
        wallet.setBalance(wallet.getBalance() + amount);
        wallet.setTotalWithdrawn(wallet.getTotalWithdrawn() + amount);
        return walletRepository.save(wallet);
    }

    @Transactional
    public void depositFunds(String username, Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than 0");
        }
        Wallet wallet = getWalletByUsername(username);
        wallet.setBalance(wallet.getBalance() + amount);
        walletRepository.save(wallet);
    }

    @Transactional
    public void withdrawFunds(String username, Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than 0");
        }
        Wallet wallet = getWalletByUsername(username);
        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance. Required: " + amount + ", Available: " + wallet.getBalance());
        }
        wallet.setBalance(wallet.getBalance() - amount);
        walletRepository.save(wallet);
    }
}
