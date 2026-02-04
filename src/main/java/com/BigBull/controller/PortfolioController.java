package com.BigBull.controller;

import com.BigBull.dto.BuyAssetRequest;
import com.BigBull.dto.PortfolioSummary;
import com.BigBull.dto.SellAssetRequest;
import com.BigBull.entity.Asset;
import com.BigBull.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/portfolio")
@CrossOrigin(origins = "*")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @GetMapping("/summary")
    public ResponseEntity<?> getPortfolioSummary(@RequestParam String username) {
        try {
            PortfolioSummary summary = portfolioService.getPortfolioSummary(username);
            return ResponseEntity.ok(summary);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse(500, "Internal server error"));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Asset>> getPortfolioList() {
        try {
            List<Asset> assets = portfolioService.getPortfolioList();
            return ResponseEntity.ok(assets);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    public static class ErrorResponse {
        private int code;
        private String message;

        public ErrorResponse(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
