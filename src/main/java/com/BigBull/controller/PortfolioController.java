package com.BigBull.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.BigBull.service.PortfolioService;

@RestController
@RequestMapping("/api/portfolio")
@CrossOrigin(origins = "*")
public class PortfolioController {
    
    @Autowired
    private PortfolioService portfolioService;
    
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getPortfolioSummary() {
        return ResponseEntity.ok(portfolioService.getPortfolioSummary());
    }
}
