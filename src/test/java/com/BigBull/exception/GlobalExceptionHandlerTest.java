package com.BigBull.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleInsufficientBalanceException_Returns400WithDetails() {
        InsufficientBalanceException ex = new InsufficientBalanceException(
            "Insufficient balance", 100.0, 50.0
        );
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        ResponseEntity<Map<String, Object>> response = handler.handleInsufficientBalanceException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INSUFFICIENT_BALANCE", response.getBody().get("errorType"));
    }

    @Test
    void testHandleAssetNotFoundException_Returns404WithAssetDetails() {
        AssetNotFoundException ex = new AssetNotFoundException("Asset not found", 1L, "RELIANCE");
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        ResponseEntity<Map<String, Object>> response = handler.handleAssetNotFoundException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ASSET_NOT_FOUND", response.getBody().get("errorType"));
    }

    @Test
    void testHandleInvalidTransactionTypeException_Returns400WithType() {
        InvalidTransactionTypeException ex = new InvalidTransactionTypeException("Invalid type", "HOLD");
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        ResponseEntity<Map<String, Object>> response = handler.handleInvalidTransactionTypeException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_TRANSACTION_TYPE", response.getBody().get("errorType"));
    }

    @Test
    void testHandleGenericException_Returns500() {
        Exception ex = new Exception("Unexpected error");
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        ResponseEntity<Map<String, Object>> response = handler.handleGlobalException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().get("errorType"));
    }
}
