package com.BigBull.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.BigBull.entity.Wallet;
import com.BigBull.exception.InsufficientBalanceException;
import com.BigBull.exception.InvalidAmountException;
import com.BigBull.repository.WalletRepository;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    private Wallet wallet;

    @BeforeEach
    void setup() {
        wallet = new Wallet(1000.0);
        wallet.setId(1L);
    }

    @Test
    void testGetOrCreateWallet_WhenWalletExists_ReturnsExisting() {
        when(walletRepository.findAll()).thenReturn(List.of(wallet));

        Wallet result = walletService.getOrCreateWallet();

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1000.0, result.getBalance());
    }

    @Test
    void testGetOrCreateWallet_WhenWalletDoesNotExist_CreatesNew() {
        when(walletRepository.findAll()).thenReturn(Collections.emptyList());
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> {
            Wallet saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        Wallet result = walletService.getOrCreateWallet();

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals(0.0, result.getBalance());
    }

    @Test
    void testDeposit_ValidAmount_IncreasesBalance() {
        when(walletRepository.findAll()).thenReturn(List.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = walletService.deposit(200.0);

        assertEquals(1200.0, result.getBalance());
    }

    @Test
    void testDeposit_InvalidAmount_ThrowsInvalidAmountException() {
        assertThrows(InvalidAmountException.class, () -> walletService.deposit(-10.0));
    }

    @Test
    void testWithdraw_ValidAmount_DecreasesBalance() {
        when(walletRepository.findAll()).thenReturn(List.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = walletService.withdraw(300.0);

        assertEquals(700.0, result.getBalance());
    }

    @Test
    void testWithdraw_InsufficientBalance_ThrowsInsufficientBalanceException() {
        when(walletRepository.findAll()).thenReturn(List.of(wallet));

        assertThrows(InsufficientBalanceException.class, () -> walletService.withdraw(2000.0));
    }

    @Test
    void testHasBalance_SufficientAmount_ReturnsTrue() {
        when(walletRepository.findAll()).thenReturn(List.of(wallet));

        boolean result = walletService.hasBalance(500.0);

        assertEquals(true, result);
    }

    @Test
    void testHasBalance_InsufficientAmount_ReturnsFalse() {
        when(walletRepository.findAll()).thenReturn(List.of(wallet));

        boolean result = walletService.hasBalance(2000.0);

        assertEquals(false, result);
    }

    @Test
    void testDeductBalance_InsufficientBalance_ThrowsException() {
        when(walletRepository.findAll()).thenReturn(List.of(wallet));

        assertThrows(InsufficientBalanceException.class, () -> walletService.deductBalance(2000.0));
    }

    @Test
    void testCreditBalance_ValidAmount_CreditsCorrectly() {
        when(walletRepository.findAll()).thenReturn(List.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = walletService.creditBalance(150.0);

        assertEquals(1150.0, result.getBalance());
    }
}
