package com.BigBull.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleInsufficientBalanceException_WithAmounts_Returns400() {
        InsufficientBalanceException ex = new InsufficientBalanceException(
            "Insufficient balance", 100.0, 50.0
        );
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        ResponseEntity<Map<String, Object>> response = handler.handleInsufficientBalanceException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INSUFFICIENT_BALANCE", response.getBody().get("errorType"));
        assertEquals(100.0, response.getBody().get("requiredAmount"));
        assertEquals(50.0, response.getBody().get("availableBalance"));
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    void testHandleInsufficientBalanceException_WithoutAmounts_Returns400() {
        InsufficientBalanceException ex = new InsufficientBalanceException("Insufficient balance");
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        ResponseEntity<Map<String, Object>> response = handler.handleInsufficientBalanceException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INSUFFICIENT_BALANCE", response.getBody().get("errorType"));
    }

    @Test
    void testHandleWalletNotFoundException_WithWalletId_Returns404() {
        WalletNotFoundException ex = new WalletNotFoundException("Wallet not found", 1L);
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        ResponseEntity<Map<String, Object>> response = handler.handleWalletNotFoundException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("WALLET_NOT_FOUND", response.getBody().get("errorType"));
        assertEquals(1L, response.getBody().get("walletId"));
        assertEquals(404, response.getBody().get("status"));
    }

    @Test
    void testHandleInvalidAmountException_WithDetails_Returns400() {
        InvalidAmountException ex = new InvalidAmountException("Invalid amount", "DEPOSIT", -50.0);
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        ResponseEntity<Map<String, Object>> response = handler.handleInvalidAmountException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_AMOUNT", response.getBody().get("errorType"));
        assertEquals("DEPOSIT", response.getBody().get("operationType"));
        assertEquals(-50.0, response.getBody().get("amount"));
    }

    @Test
    void testHandleAssetNotFoundException_WithAssetIdAndSymbol_Returns404() {
        AssetNotFoundException ex = new AssetNotFoundException("Asset not found", 1L, "RELIANCE");
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        ResponseEntity<Map<String, Object>> response = handler.handleAssetNotFoundException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ASSET_NOT_FOUND", response.getBody().get("errorType"));
        assertEquals(1L, response.getBody().get("assetId"));
        assertEquals("RELIANCE", response.getBody().get("symbol"));
    }

    @Test
    void testHandleInsufficientAssetException_WithQuantities_Returns400() {
        InsufficientAssetException ex = new InsufficientAssetException(
            "Insufficient asset", "RELIANCE", 100.0, 50.0
        );
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        ResponseEntity<Map<String, Object>> response = handler.handleInsufficientAssetException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INSUFFICIENT_ASSET", response.getBody().get("errorType"));
        assertEquals("RELIANCE", response.getBody().get("symbol"));
        assertEquals(100.0, response.getBody().get("requiredQuantity"));
        assertEquals(50.0, response.getBody().get("availableQuantity"));
    }

    @Test
    void testHandleInvalidAssetQuantityException_Returns400() {
        InvalidAssetQuantityException ex = new InvalidAssetQuantityException("Invalid quantity", -10.0);
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        ResponseEntity<Map<String, Object>> response = handler.handleInvalidAssetQuantityException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_ASSET_QUANTITY", response.getBody().get("errorType"));
        assertEquals(-10.0, response.getBody().get("invalidQuantity"));
    }

    @Test
    void testHandleTransactionNotFoundException_Returns404() {
        TransactionNotFoundException ex = new TransactionNotFoundException("Transaction not found", 1L);
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        ResponseEntity<Map<String, Object>> response = handler.handleTransactionNotFoundException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("TRANSACTION_NOT_FOUND", response.getBody().get("errorType"));
        assertEquals(1L, response.getBody().get("transactionId"));
    }

    @Test
    void testHandleInvalidTransactionTypeException_Returns400() {
        InvalidTransactionTypeException ex = new InvalidTransactionTypeException("Invalid type", "HOLD");
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        ResponseEntity<Map<String, Object>> response = handler.handleInvalidTransactionTypeException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_TRANSACTION_TYPE", response.getBody().get("errorType"));
        assertEquals("HOLD", response.getBody().get("invalidType"));
    }

    @Test
    void testHandleTransactionFailedException_Returns500() {
        TransactionFailedException ex = new TransactionFailedException(
            "Transaction failed", "Database connection lost"
        );
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        ResponseEntity<Map<String, Object>> response = handler.handleTransactionFailedException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("TRANSACTION_FAILED", response.getBody().get("errorType"));
        assertEquals("Database connection lost", response.getBody().get("failureReason"));
    }

    @Test
    void testHandlePortfolioNotFoundException_Returns404() {
        PortfolioNotFoundException ex = new PortfolioNotFoundException("Portfolio not found", 1L);
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        ResponseEntity<Map<String, Object>> response = handler.handlePortfolioNotFoundException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("PORTFOLIO_NOT_FOUND", response.getBody().get("errorType"));
        assertEquals(1L, response.getBody().get("portfolioId"));
    }

    @Test
    void testHandleMarketDataUnavailableException_Returns503() {
        MarketDataUnavailableException ex = new MarketDataUnavailableException(
            "Market data unavailable", "RELIANCE"
        );
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        ResponseEntity<Map<String, Object>> response = handler.handleMarketDataUnavailableException(ex, request);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("MARKET_DATA_UNAVAILABLE", response.getBody().get("errorType"));
        assertEquals("RELIANCE", response.getBody().get("symbol"));
        assertEquals(503, response.getBody().get("status"));
    }

    @Test
    void testHandlePriceDataNotFoundException_Returns404() {
        PriceDataNotFoundException ex = new PriceDataNotFoundException("Price data not found", "RELIANCE");
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        ResponseEntity<Map<String, Object>> response = handler.handlePriceDataNotFoundException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("PRICE_DATA_NOT_FOUND", response.getBody().get("errorType"));
        assertEquals("RELIANCE", response.getBody().get("symbol"));
    }

    @Test
    void testHandleInvalidOrderException_Returns400() {
        InvalidOrderException ex = new InvalidOrderException("Invalid order", "Price must be positive");
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        ResponseEntity<Map<String, Object>> response = handler.handleInvalidOrderException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_ORDER", response.getBody().get("errorType"));
        assertEquals("Price must be positive", response.getBody().get("validationFailureReason"));
    }

    @Test
    void testHandleOrderExecutionException_Returns500() {
        OrderExecutionException ex = new OrderExecutionException(
            "Order execution failed", 123L, "Insufficient liquidity"
        );
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        ResponseEntity<Map<String, Object>> response = handler.handleOrderExecutionException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ORDER_EXECUTION_FAILED", response.getBody().get("errorType"));
        assertEquals(123L, response.getBody().get("orderId"));
        assertEquals("Insufficient liquidity", response.getBody().get("failureReason"));
    }

    @Test
    void testHandleGenericException_Returns500() {
        Exception ex = new Exception("Unexpected error");
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        ResponseEntity<Map<String, Object>> response = handler.handleGlobalException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().get("errorType"));
        assertTrue(response.getBody().get("message").toString().contains("Unexpected error"));
    }

    @Test
    void testErrorResponse_ContainsAllStandardFields() {
        Exception ex = new Exception("Test");
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setRequestURI("/api/test");
        ServletWebRequest request = new ServletWebRequest(mockRequest);

        ResponseEntity<Map<String, Object>> response = handler.handleGlobalException(ex, request);

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("timestamp"));
        assertTrue(body.containsKey("status"));
        assertTrue(body.containsKey("error"));
        assertTrue(body.containsKey("errorType"));
        assertTrue(body.containsKey("message"));
        assertTrue(body.containsKey("path"));
    }
}
