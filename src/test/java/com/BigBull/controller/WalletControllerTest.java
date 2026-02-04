package com.BigBull.controller;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
            .andExpect(status().isOk());
    }

    @Test
    void testDeposit_ValidAmount_Returns200() throws Exception {
        Wallet wallet = new Wallet(1200.0);
        when(walletService.deposit(anyDouble())).thenReturn(wallet);

        mockMvc.perform(post("/api/wallet/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": 200}"))
            .andExpect(status().isOk());
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
        when(walletService.withdraw(anyDouble())).thenReturn(wallet);

        mockMvc.perform(post("/api/wallet/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": 200}"))
            .andExpect(status().isOk());
    }

    @Test
    void testWithdraw_InsufficientBalance_Returns400WithErrorResponse() throws Exception {
        when(walletService.withdraw(anyDouble())).thenThrow(new InsufficientBalanceException("Insufficient balance"));

        mockMvc.perform(post("/api/wallet/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": 2000}"))
            .andExpect(status().isBadRequest());
    }
}
