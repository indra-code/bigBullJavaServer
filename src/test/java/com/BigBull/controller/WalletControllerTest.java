package com.BigBull.controller;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.BigBull.entity.Wallet;
import com.BigBull.exception.InsufficientBalanceException;
import com.BigBull.service.WalletService;

@WebMvcTest(WalletController.class)
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @Test
    void testGetWallet_ReturnsWalletDetails() throws Exception {
        Wallet wallet = new Wallet(1000.0);
        wallet.setId(1L);
        when(walletService.getWallet()).thenReturn(wallet);

        mockMvc.perform(get("/api/wallet"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.balance", is(1000.0)));

        verify(walletService).getWallet();
    }

    @Test
    void testDeposit_ValidAmount_Returns200() throws Exception {
        Wallet wallet = new Wallet(1200.0);
        wallet.setId(1L);
        when(walletService.deposit(200.0)).thenReturn(wallet);

        mockMvc.perform(post("/api/wallet/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": 200}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.balance", is(1200.0)));

        verify(walletService).deposit(200.0);
    }

    @Test
    void testDeposit_InvalidAmount_Returns400() throws Exception {
        mockMvc.perform(post("/api/wallet/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": -10}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testWithdraw_ValidAmount_Returns200() throws Exception {
        Wallet wallet = new Wallet(800.0);
        wallet.setId(1L);
        when(walletService.withdraw(200.0)).thenReturn(wallet);

        mockMvc.perform(post("/api/wallet/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": 200}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.balance", is(800.0)));

        verify(walletService).withdraw(200.0);
    }

    @Test
    void testWithdraw_InsufficientBalance_Returns400WithErrorResponse() throws Exception {
        when(walletService.withdraw(2000.0)).thenThrow(
            new InsufficientBalanceException("Insufficient balance", 2000.0, 1000.0)
        );

        mockMvc.perform(post("/api/wallet/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": 2000}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorType", is("INSUFFICIENT_BALANCE")));

        verify(walletService).withdraw(2000.0);
    }

    @Test
    void testWithdraw_ZeroAmount_Returns400() throws Exception {
        mockMvc.perform(post("/api/wallet/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": 0}"))
            .andExpect(status().isBadRequest());
    }
}
