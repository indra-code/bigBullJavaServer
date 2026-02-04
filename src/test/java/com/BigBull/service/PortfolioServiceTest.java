package com.BigBull.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.BigBull.entity.Asset;
import com.BigBull.entity.Transaction;
import com.BigBull.exception.MarketDataUnavailableException;
import com.BigBull.exception.PriceDataNotFoundException;
import com.BigBull.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
public class PortfolioServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private RestTemplate restTemplate;

    private PortfolioService portfolioService;

    @BeforeEach
    void setup() {
        portfolioService = new PortfolioService();
        ReflectionTestUtils.setField(portfolioService, "transactionRepository", transactionRepository);
        ReflectionTestUtils.setField(portfolioService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(portfolioService, "FLASK_BASE_URL", "http://localhost:5000");
    }

    @Test
    void testGetPortfolioSummary_EmptyPortfolio_ReturnsZeroValues() {
        when(transactionRepository.findAll()).thenReturn(List.of());

        Map<String, Object> result = portfolioService.getPortfolioSummary();

        assertEquals(0.0, (Double) result.get("totalInvested"));
        assertEquals(0.0, (Double) result.get("totalCurrentValue"));
        assertEquals(0.0, (Double) result.get("totalPnl"));
    }

    @Test
    void testGetPortfolioSummary_SingleAsset_CalculatesCorrectly() {
        Asset asset = new Asset("STOCK", "Reliance Industries", "RELIANCE");
        Transaction tx = new Transaction(asset, "BUY", 10.0, 100.0, LocalDateTime.now());
        when(transactionRepository.findAll()).thenReturn(List.of(tx));

        Map<String, Object> priceResponse = new HashMap<>();
        priceResponse.put("price", 110.0);
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(priceResponse);

        Map<String, Object> result = portfolioService.getPortfolioSummary();

        assertEquals(1000.0, (Double) result.get("totalInvested"));
        assertEquals(1100.0, (Double) result.get("totalCurrentValue"));
        assertEquals(100.0, (Double) result.get("totalPnl"));
    }

    @Test
    void testGetCurrentPrice_InvalidSymbol_ThrowsPriceDataNotFoundException() {
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(new HashMap<>());

        assertThrows(PriceDataNotFoundException.class, () -> {
            ReflectionTestUtils.invokeMethod(portfolioService, "getCurrentPrice", "INVALID");
        });
    }

    @Test
    void testGetCurrentPrice_FlaskServerDown_ThrowsMarketDataUnavailableException() {
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenThrow(new RuntimeException("Flask down"));

        assertThrows(MarketDataUnavailableException.class, () -> {
            ReflectionTestUtils.invokeMethod(portfolioService, "getCurrentPrice", "RELIANCE");
        });
    }
}
