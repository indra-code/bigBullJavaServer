package com.BigBull.controller;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

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

    @Test
    void testGetTransactionById_Found_Returns200() throws Exception {
        Asset asset = new Asset("STOCK", "Reliance Industries", "RELIANCE");
        Transaction tx = new Transaction(asset, "BUY", 2.0, 200.0, LocalDateTime.now());
        when(transactionService.getTransactionById(1L)).thenReturn(java.util.Optional.of(tx));

        mockMvc.perform(get("/api/transactions/1"))
            .andExpect(status().isOk());
    }

    @Test
    void testGetTransactionById_NotFound_Returns404() throws Exception {
        when(transactionService.getTransactionById(999L)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/transactions/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetTransactionsByAssetId_Returns200() throws Exception {
        Asset asset = new Asset("STOCK", "Reliance Industries", "RELIANCE");
        Transaction tx = new Transaction(asset, "BUY", 2.0, 200.0, LocalDateTime.now());
        when(transactionService.getTransactionsByAssetId(1L)).thenReturn(List.of(tx));

        mockMvc.perform(get("/api/transactions/asset/1"))
            .andExpect(status().isOk());
    }

    @Test
    void testGetTransactionsBySymbol_Returns200() throws Exception {
        Asset asset = new Asset("STOCK", "Reliance Industries", "RELIANCE");
        Transaction tx = new Transaction(asset, "BUY", 2.0, 200.0, LocalDateTime.now());
        when(transactionService.getTransactionsBySymbol("RELIANCE")).thenReturn(List.of(tx));

        mockMvc.perform(get("/api/transactions/symbol/RELIANCE"))
            .andExpect(status().isOk());
    }

    @Test
    void testCreateTransaction_RuntimeException_Returns400() throws Exception {
        when(transactionService.createTransaction(anyLong(), anyString(), anyDouble(), anyDouble()))
            .thenThrow(new RuntimeException("Business error"));

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"assetId\":1,\"type\":\"BUY\",\"quantity\":2,\"price\":200}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", is("Business error")));
    }

    @Test
    void testCreateTransaction_GenericException_Returns500() throws Exception {
        when(transactionService.createTransaction(anyLong(), anyString(), anyDouble(), anyDouble()))
            .thenThrow(new IllegalStateException("Unexpected"));

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"assetId\":1,\"type\":\"BUY\",\"quantity\":2,\"price\":200}"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void testBuyStock_NumberFormatException_Returns400() throws Exception {
        mockMvc.perform(post("/api/transactions/buy")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"symbol\":\"RELIANCE\",\"name\":\"Reliance Industries\",\"quantity\":\"abc\",\"price\":200}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", is("Invalid number format for quantity or price")));
    }

    @Test
    void testSellStock_RuntimeException_Returns400() throws Exception {
        when(transactionService.sellStock(anyString(), anyDouble(), anyDouble()))
            .thenThrow(new RuntimeException("Sell error"));

        mockMvc.perform(post("/api/transactions/sell")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"symbol\":\"RELIANCE\",\"quantity\":2,\"price\":250}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", is("Sell error")));
    }

    @Test
    void testSellStock_GenericException_Returns500() throws Exception {
        when(transactionService.sellStock(anyString(), anyDouble(), anyDouble()))
            .thenThrow(new IllegalStateException("Unexpected"));

        mockMvc.perform(post("/api/transactions/sell")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"symbol\":\"RELIANCE\",\"quantity\":2,\"price\":250}"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void testDeleteTransaction_Returns204() throws Exception {
        doNothing().when(transactionService).deleteTransaction(1L);

        mockMvc.perform(delete("/api/transactions/1"))
            .andExpect(status().isNoContent());

        verify(transactionService).deleteTransaction(1L);
    }
}
