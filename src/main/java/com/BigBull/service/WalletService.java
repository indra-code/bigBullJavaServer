package com.BigBull.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.BigBull.entity.Wallet;
import com.BigBull.exception.InsufficientBalanceException;
import com.BigBull.exception.InvalidAmountException;
import com.BigBull.repository.WalletRepository;

@Service
public class WalletService {
    
    private static final Logger logger = LoggerFactory.getLogger(WalletService.class);
    
    @Autowired
    private WalletRepository walletRepository;
    
    public Wallet getOrCreateWallet() {
        logger.debug("Attempting to retrieve or create wallet");
        try {
            List<Wallet> wallets = walletRepository.findAll();
            if (wallets.isEmpty()) {
                logger.info("No wallet found. Creating new wallet with initial balance 0.0");
                Wallet wallet = new Wallet(0.0);
                Wallet savedWallet = walletRepository.save(wallet);
                logger.info("Wallet created successfully with ID: {}", savedWallet.getId());
                return savedWallet;
            }
            logger.debug("Wallet retrieved successfully with ID: {} and balance: {}", 
                wallets.get(0).getId(), wallets.get(0).getBalance());
            return wallets.get(0);
        } catch (Exception e) {
            logger.error("Error retrieving or creating wallet", e);
            throw e;
        }
    }
    
    public Wallet getWallet() {
        logger.debug("Fetching wallet");
        return getOrCreateWallet();
    }
    
    public Wallet deposit(Double amount) {
        logger.debug("Deposit operation initiated with amount: {}", amount);
        
        if (amount == null || amount <= 0) {
            logger.error("Invalid deposit amount: {}. Amount must be positive", amount);
            throw new InvalidAmountException(
                "Deposit amount must be positive", 
                "DEPOSIT", 
                amount
            );
        }
        
        try {
            Wallet wallet = getOrCreateWallet();
            Double previousBalance = wallet.getBalance();
            wallet.deposit(amount);
            Wallet savedWallet = walletRepository.save(wallet);
            logger.info("Deposit successful. Wallet ID: {}, Amount: {}, Previous Balance: {}, New Balance: {}", 
                wallet.getId(), amount, previousBalance, savedWallet.getBalance());
            return savedWallet;
        } catch (Exception e) {
            logger.error("Error during deposit operation with amount: {}", amount, e);
            throw e;
        }
    }
    
    public Wallet withdraw(Double amount) {
        logger.debug("Withdraw operation initiated with amount: {}", amount);
        
        if (amount == null || amount <= 0) {
            logger.error("Invalid withdrawal amount: {}. Amount must be positive", amount);
            throw new InvalidAmountException(
                "Withdrawal amount must be positive", 
                "WITHDRAW", 
                amount
            );
        }
        
        try {
            Wallet wallet = getOrCreateWallet();
            if (!wallet.hasBalance(amount)) {
                logger.error("Insufficient balance for withdrawal. Required: {}, Available: {}", 
                    amount, wallet.getBalance());
                throw new InsufficientBalanceException(
                    "Insufficient wallet balance for withdrawal",
                    amount,
                    wallet.getBalance()
                );
            }
            Double previousBalance = wallet.getBalance();
            wallet.withdraw(amount);
            Wallet savedWallet = walletRepository.save(wallet);
            logger.info("Withdrawal successful. Wallet ID: {}, Amount: {}, Previous Balance: {}, New Balance: {}", 
                wallet.getId(), amount, previousBalance, savedWallet.getBalance());
            return savedWallet;
        } catch (Exception e) {
            logger.error("Error during withdrawal operation with amount: {}", amount, e);
            throw e;
        }
    }
    
    public boolean hasBalance(Double amount) {
        logger.debug("Checking if wallet has sufficient balance for amount: {}", amount);
        try {
            Wallet wallet = getOrCreateWallet();
            boolean hasSufficientBalance = wallet.hasBalance(amount);
            logger.debug("Balance check result: Required Amount: {}, Available: {}, Has Sufficient Balance: {}", 
                amount, wallet.getBalance(), hasSufficientBalance);
            return hasSufficientBalance;
        } catch (Exception e) {
            logger.error("Error checking wallet balance for amount: {}", amount, e);
            throw e;
        }
    }
    
    public Wallet deductBalance(Double amount) {
        logger.debug("Deducting balance from wallet with amount: {}", amount);
        
        if (amount == null || amount <= 0) {
            logger.error("Invalid deduction amount: {}. Amount must be positive", amount);
            throw new InvalidAmountException(
                "Deduction amount must be positive", 
                "DEDUCT", 
                amount
            );
        }
        
        try {
            Wallet wallet = getOrCreateWallet();
            if (!wallet.hasBalance(amount)) {
                logger.error("Insufficient balance for deduction. Required: {}, Available: {}", 
                    amount, wallet.getBalance());
                throw new InsufficientBalanceException(
                    "Insufficient wallet balance for deduction",
                    amount,
                    wallet.getBalance()
                );
            }
            Double previousBalance = wallet.getBalance();
            wallet.withdraw(amount);
            Wallet savedWallet = walletRepository.save(wallet);
            logger.info("Balance deducted successfully. Amount: {}, Previous Balance: {}, New Balance: {}", 
                amount, previousBalance, savedWallet.getBalance());
            return savedWallet;
        } catch (InsufficientBalanceException | InvalidAmountException e) {
            logger.error("Error deducting balance with amount: {}", amount, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error deducting balance with amount: {}", amount, e);
            throw e;
        }
    }
    
    public Wallet creditBalance(Double amount) {
        logger.debug("Crediting balance to wallet with amount: {}", amount);
        
        if (amount == null || amount <= 0) {
            logger.error("Invalid credit amount: {}. Amount must be positive", amount);
            throw new InvalidAmountException(
                "Credit amount must be positive", 
                "CREDIT", 
                amount
            );
        }
        
        try {
            Wallet wallet = getOrCreateWallet();
            Double previousBalance = wallet.getBalance();
            wallet.deposit(amount);
            Wallet savedWallet = walletRepository.save(wallet);
            logger.info("Balance credited successfully. Amount: {}, Previous Balance: {}, New Balance: {}", 
                amount, previousBalance, savedWallet.getBalance());
            return savedWallet;
        } catch (Exception e) {
            logger.error("Error crediting balance with amount: {}", amount, e);
            throw e;
        }
    }
}
