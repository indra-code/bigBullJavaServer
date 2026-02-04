package com.BigBull.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.BigBull.entity.Asset;
import com.BigBull.entity.Transaction;
import com.BigBull.exception.AssetNotFoundException;
import com.BigBull.exception.InsufficientAssetException;
import com.BigBull.exception.InsufficientBalanceException;
import com.BigBull.exception.InvalidTransactionTypeException;
import com.BigBull.exception.TransactionFailedException;
import com.BigBull.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AssetService assetService;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private TransactionService transactionService;

    private Asset asset;

    @BeforeEach
    void setup() {
        asset = new Asset("STOCK", "Reliance Industries", "RELIANCE");
        asset.setId(1L);
        asset.setQuantity(10.0);
        asset.setCostPerUnit(200.0);
    }

    @Test
    void testGetAllTransactions_ReturnsAllTransactions() {
        Transaction tx = new Transaction(asset, "BUY", 2.0, 200.0, LocalDateTime.now());
        when(transactionRepository.findAll()).thenReturn(List.of(tx));

        List<Transaction> result = transactionService.getAllTransactions();

        assertEquals(1, result.size());
    }

    @Test
    void testCreateTransaction_BuyTransaction_Success() {
        when(assetService.getAssetById(1L)).thenReturn(Optional.of(asset));
        when(walletService.hasBalance(1000.0)).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Transaction result = transactionService.createTransaction(1L, "BUY", 5.0, 200.0);

        assertNotNull(result);
        assertEquals("BUY", result.getType());
    }

    @Test
    void testCreateTransaction_BuyTransaction_InsufficientBalance_ThrowsException() {
        when(assetService.getAssetById(1L)).thenReturn(Optional.of(asset));
        when(walletService.hasBalance(1000.0)).thenReturn(false);
        when(walletService.getWallet()).thenReturn(new com.BigBull.entity.Wallet(500.0));

        assertThrows(InsufficientBalanceException.class, () -> 
            transactionService.createTransaction(1L, "BUY", 5.0, 200.0)
        );
    }

    @Test
    void testCreateTransaction_SellTransaction_InsufficientAsset_ThrowsException() {
        when(assetService.getAssetById(1L)).thenReturn(Optional.of(asset));

        assertThrows(InsufficientAssetException.class, () -> 
            transactionService.createTransaction(1L, "SELL", 50.0, 200.0)
        );
    }

    @Test
    void testCreateTransaction_InvalidType_ThrowsInvalidTransactionTypeException() {
        assertThrows(InvalidTransactionTypeException.class, () -> 
            transactionService.createTransaction(1L, "INVALID", 5.0, 200.0)
        );
    }

    @Test
    void testCreateTransaction_InvalidQuantity_ThrowsTransactionFailedException() {
        assertThrows(TransactionFailedException.class, () -> 
            transactionService.createTransaction(1L, "BUY", -1.0, 200.0)
        );
    }

    @Test
    void testBuyStock_NewAsset_CreatesAndBuys() {
        Asset newAsset = new Asset("STOCK", "Reliance Industries", "RELIANCE");
        when(assetService.getAssetBySymbol("RELIANCE")).thenReturn(Optional.empty());
        when(assetService.createAsset(eq("STOCK"), eq("Reliance Industries"), eq("RELIANCE"))).thenReturn(newAsset);
        when(assetService.saveAsset(any(Asset.class))).thenAnswer(invocation -> {
            Asset saved = invocation.getArgument(0);
            if (saved.getId() == null) {
                saved.setId(2L);
            }
            return saved;
        });
        when(walletService.hasBalance(1000.0)).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Transaction result = transactionService.buyStock("RELIANCE", "Reliance Industries", 5.0, 200.0);

        assertNotNull(result);
        assertEquals("BUY", result.getType());
    }

    @Test
    void testBuyStock_InsufficientBalance_ThrowsInsufficientBalanceException() {
        when(walletService.hasBalance(1000.0)).thenReturn(false);
        when(walletService.getWallet()).thenReturn(new com.BigBull.entity.Wallet(500.0));

        assertThrows(InsufficientBalanceException.class, () -> 
            transactionService.buyStock("RELIANCE", "Reliance Industries", 5.0, 200.0)
        );
    }

    @Test
    void testSellStock_NonExistentAsset_ThrowsAssetNotFoundException() {
        when(assetService.getAssetBySymbol("RELIANCE")).thenReturn(Optional.empty());

        assertThrows(AssetNotFoundException.class, () -> 
            transactionService.sellStock("RELIANCE", 2.0, 250.0)
        );
    }

    @Test
    void testSellStock_InsufficientQuantity_ThrowsInsufficientAssetException() {
        when(assetService.getAssetBySymbol("RELIANCE")).thenReturn(Optional.of(asset));

        assertThrows(InsufficientAssetException.class, () -> 
            transactionService.sellStock("RELIANCE", 50.0, 250.0)
        );
    }
}
