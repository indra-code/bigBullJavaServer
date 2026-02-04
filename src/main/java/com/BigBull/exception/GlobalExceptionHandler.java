package com.BigBull.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Global exception handler for centralized error handling across the application.
 * Provides consistent JSON error responses with appropriate HTTP status codes.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handle InsufficientBalanceException
     */
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientBalanceException(
            InsufficientBalanceException ex, WebRequest request) {
        
        Map<String, Object> errorResponse = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "INSUFFICIENT_BALANCE",
            ex.getMessage(),
            request
        );
        
        if (ex.getRequiredAmount() != null && ex.getAvailableBalance() != null) {
            errorResponse.put("requiredAmount", ex.getRequiredAmount());
            errorResponse.put("availableBalance", ex.getAvailableBalance());
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle WalletNotFoundException
     */
    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleWalletNotFoundException(
            WalletNotFoundException ex, WebRequest request) {
        
        Map<String, Object> errorResponse = buildErrorResponse(
            HttpStatus.NOT_FOUND,
            "WALLET_NOT_FOUND",
            ex.getMessage(),
            request
        );
        
        if (ex.getWalletId() != null) {
            errorResponse.put("walletId", ex.getWalletId());
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handle InvalidAmountException
     */
    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidAmountException(
            InvalidAmountException ex, WebRequest request) {
        
        Map<String, Object> errorResponse = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "INVALID_AMOUNT",
            ex.getMessage(),
            request
        );
        
        if (ex.getOperationType() != null) {
            errorResponse.put("operationType", ex.getOperationType());
        }
        if (ex.getAmount() != null) {
            errorResponse.put("amount", ex.getAmount());
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle AssetNotFoundException
     */
    @ExceptionHandler(AssetNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAssetNotFoundException(
            AssetNotFoundException ex, WebRequest request) {
        
        Map<String, Object> errorResponse = buildErrorResponse(
            HttpStatus.NOT_FOUND,
            "ASSET_NOT_FOUND",
            ex.getMessage(),
            request
        );
        
        if (ex.getAssetId() != null) {
            errorResponse.put("assetId", ex.getAssetId());
        }
        if (ex.getSymbol() != null) {
            errorResponse.put("symbol", ex.getSymbol());
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handle InsufficientAssetException
     */
    @ExceptionHandler(InsufficientAssetException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientAssetException(
            InsufficientAssetException ex, WebRequest request) {
        
        Map<String, Object> errorResponse = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "INSUFFICIENT_ASSET",
            ex.getMessage(),
            request
        );
        
        if (ex.getSymbol() != null) {
            errorResponse.put("symbol", ex.getSymbol());
        }
        if (ex.getRequiredQuantity() != null) {
            errorResponse.put("requiredQuantity", ex.getRequiredQuantity());
        }
        if (ex.getAvailableQuantity() != null) {
            errorResponse.put("availableQuantity", ex.getAvailableQuantity());
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle InvalidAssetQuantityException
     */
    @ExceptionHandler(InvalidAssetQuantityException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidAssetQuantityException(
            InvalidAssetQuantityException ex, WebRequest request) {
        
        Map<String, Object> errorResponse = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "INVALID_ASSET_QUANTITY",
            ex.getMessage(),
            request
        );
        
        if (ex.getInvalidQuantity() != null) {
            errorResponse.put("invalidQuantity", ex.getInvalidQuantity());
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle TransactionNotFoundException
     */
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTransactionNotFoundException(
            TransactionNotFoundException ex, WebRequest request) {
        
        Map<String, Object> errorResponse = buildErrorResponse(
            HttpStatus.NOT_FOUND,
            "TRANSACTION_NOT_FOUND",
            ex.getMessage(),
            request
        );
        
        if (ex.getTransactionId() != null) {
            errorResponse.put("transactionId", ex.getTransactionId());
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handle InvalidTransactionTypeException
     */
    @ExceptionHandler(InvalidTransactionTypeException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTransactionTypeException(
            InvalidTransactionTypeException ex, WebRequest request) {
        
        Map<String, Object> errorResponse = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "INVALID_TRANSACTION_TYPE",
            ex.getMessage(),
            request
        );
        
        if (ex.getInvalidType() != null) {
            errorResponse.put("invalidType", ex.getInvalidType());
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle TransactionFailedException
     */
    @ExceptionHandler(TransactionFailedException.class)
    public ResponseEntity<Map<String, Object>> handleTransactionFailedException(
            TransactionFailedException ex, WebRequest request) {
        
        Map<String, Object> errorResponse = buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "TRANSACTION_FAILED",
            ex.getMessage(),
            request
        );
        
        if (ex.getFailureReason() != null) {
            errorResponse.put("failureReason", ex.getFailureReason());
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Handle PortfolioNotFoundException
     */
    @ExceptionHandler(PortfolioNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePortfolioNotFoundException(
            PortfolioNotFoundException ex, WebRequest request) {
        
        Map<String, Object> errorResponse = buildErrorResponse(
            HttpStatus.NOT_FOUND,
            "PORTFOLIO_NOT_FOUND",
            ex.getMessage(),
            request
        );
        
        if (ex.getPortfolioId() != null) {
            errorResponse.put("portfolioId", ex.getPortfolioId());
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handle MarketDataUnavailableException
     */
    @ExceptionHandler(MarketDataUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleMarketDataUnavailableException(
            MarketDataUnavailableException ex, WebRequest request) {
        
        Map<String, Object> errorResponse = buildErrorResponse(
            HttpStatus.SERVICE_UNAVAILABLE,
            "MARKET_DATA_UNAVAILABLE",
            ex.getMessage(),
            request
        );
        
        if (ex.getSymbol() != null) {
            errorResponse.put("symbol", ex.getSymbol());
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    /**
     * Handle PriceDataNotFoundException
     */
    @ExceptionHandler(PriceDataNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePriceDataNotFoundException(
            PriceDataNotFoundException ex, WebRequest request) {
        
        Map<String, Object> errorResponse = buildErrorResponse(
            HttpStatus.NOT_FOUND,
            "PRICE_DATA_NOT_FOUND",
            ex.getMessage(),
            request
        );
        
        if (ex.getSymbol() != null) {
            errorResponse.put("symbol", ex.getSymbol());
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handle InvalidOrderException
     */
    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidOrderException(
            InvalidOrderException ex, WebRequest request) {
        
        Map<String, Object> errorResponse = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "INVALID_ORDER",
            ex.getMessage(),
            request
        );
        
        if (ex.getValidationFailureReason() != null) {
            errorResponse.put("validationFailureReason", ex.getValidationFailureReason());
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle OrderExecutionException
     */
    @ExceptionHandler(OrderExecutionException.class)
    public ResponseEntity<Map<String, Object>> handleOrderExecutionException(
            OrderExecutionException ex, WebRequest request) {
        
        Map<String, Object> errorResponse = buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "ORDER_EXECUTION_FAILED",
            ex.getMessage(),
            request
        );
        
        if (ex.getOrderId() != null) {
            errorResponse.put("orderId", ex.getOrderId());
        }
        if (ex.getFailureReason() != null) {
            errorResponse.put("failureReason", ex.getFailureReason());
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Handle generic exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        
        Map<String, Object> errorResponse = buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred: " + ex.getMessage(),
            request
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Build error response with common fields
     */
    private Map<String, Object> buildErrorResponse(
            HttpStatus status, String errorType, String message, WebRequest request) {
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("errorType", errorType);
        errorResponse.put("message", message);
        errorResponse.put("path", request.getDescription(false).replace("uri=", ""));
        
        return errorResponse;
    }
}
