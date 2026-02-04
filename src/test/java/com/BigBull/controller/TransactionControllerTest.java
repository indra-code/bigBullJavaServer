package com.BigBull.controller;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.BigBull.entity.Asset;
import com.BigBull.entity.Transaction;
import com.BigBull.exception.InsufficientAssetException;
import com.BigBull.service.TransactionService;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Test
    void testGetAllTransactions_Returns200WithList() throws Exception {
        Asset asset = new Asset("STOCK", "Reliance Industries", "RELIANCE");
        Transaction tx = new Transaction(asset, "BUY", 2.0, 200.0, LocalDateTime.now());
        when(transactionService.getAllTransactions()).thenReturn(List.of(tx));

        mockMvc.perform(get("/api/transactions"))
            .andExpect(status().isOk());
    }

    @Test
    void testCreateTransaction_ValidBuy_Returns200() throws Exception {
        Asset asset = new Asset("STOCK", "Reliance Industries", "RELIANCE");
        Transaction tx = new Transaction(asset, "BUY", 2.0, 200.0, LocalDateTime.now());
        when(transactionService.createTransaction(anyLong(), anyString(), anyDouble(), anyDouble())).thenReturn(tx);

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"assetId\":1,\"type\":\"BUY\",\"quantity\":2,\"price\":200}"))
            .andExpect(status().isOk());
    }

    @Test
    void testBuyStock_ValidRequest_Returns200() throws Exception {
        Asset asset = new Asset("STOCK", "Reliance Industries", "RELIANCE");
        Transaction tx = new Transaction(asset, "BUY", 5.0, 200.0, LocalDateTime.now());
        when(transactionService.buyStock(anyString(), anyString(), anyDouble(), anyDouble())).thenReturn(tx);

        mockMvc.perform(post("/api/transactions/buy")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"symbol\":\"RELIANCE\",\"name\":\"Reliance Industries\",\"quantity\":5,\"price\":200}"))
            .andExpect(status().isOk());
    }

    @Test
    void testBuyStock_MissingFields_Returns400() throws Exception {
        mockMvc.perform(post("/api/transactions/buy")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"symbol\":\"RELIANCE\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testSellStock_InsufficientAsset_Returns400WithStructuredError() throws Exception {
        when(transactionService.sellStock(anyString(), anyDouble(), anyDouble()))
            .thenThrow(new InsufficientAssetException("Insufficient asset", "RELIANCE", 2.0, 1.0));

        mockMvc.perform(post("/api/transactions/sell")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"symbol\":\"RELIANCE\",\"quantity\":2,\"price\":250}"))
            .andExpect(status().isBadRequest());
    }
}
